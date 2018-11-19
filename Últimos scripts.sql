-- CUENTAS DE CLIENTES
-- | Cod cliente |Cliente + cel | Limite | Fec Ult pago | Crédito | Docs Vencidos | Interés | Docs a vencer | Saldo + interes |
-- Tablas: venta_det_cuotas, cliente
SELECT DISTINCT v.cod_cliente, c.razon_soc || ' - ' || c.telefono AS cliente, c.limite_credito, 
(SELECT to_char(MAX(fec_recibo), 'dd/MM/yyyy') FROM venta_det_cuotas WHERE cod_cliente = v.cod_cliente) AS ultimo_pago, 
(SELECT SUM(monto_cuota) FROM venta_det_cuotas WHERE cod_cliente = v.cod_cliente AND tip_comprob IN ('NCC', 'CRE') AND nro_pago = 0 
AND estado = 'V') AS credito, 
(SELECT SUM(monto_cuota) FROM venta_det_cuotas WHERE cod_cliente = v.cod_cliente 
AND current_date - fec_vencimiento::date > 0 AND nro_pago = 0 AND tip_comprob IN ('DEB', 'FAI') AND estado = 'V') AS vencidos, 
(SELECT SUM(monto_cuota) FROM venta_det_cuotas WHERE cod_cliente = v.cod_cliente 
AND current_date - fec_vencimiento::date <= 0 AND nro_pago = 0 AND tip_comprob IN ('DEB', 'FAI') AND estado = 'V') AS por_vencer
FROM venta_det_cuotas v
INNER JOIN cliente c
ON v.cod_cliente = c.cod_cliente 
WHERE v.cod_cliente = 2


-- docs pendientes de pago de clientes 
SELECT DISTINCT nro_comprob, tip_comprob, nro_cuota, can_cuota, to_char(fec_comprob, 'dd/MM/yyyy') AS fec_emision, 
to_char(fec_vencimiento, 'dd/MM/yyyy') AS fec_vencimiento, 
CASE WHEN current_date - fec_vencimiento::date <= 0 THEN 0 ELSE current_date - fec_vencimiento::date END AS dias_vencidos, 
monto_cuota 
FROM venta_det_cuotas 
WHERE cod_cliente = 2 
AND nro_pago = 0 
AND estado = 'V' 
ORDER BY nro_comprob

-- script para calculo de interes

SELECT nro_comprob, monto_cuota, tip_comprob,
CASE WHEN current_date - fec_vencimiento::date <= 0 THEN 0 ELSE current_date - fec_vencimiento::date END AS dias_vencidos
FROM venta_det_cuotas
WHERE cod_cliente = 2
AND nro_pago = 0
AND estado = 'V'
ORDER BY dias_vencidos DESC

