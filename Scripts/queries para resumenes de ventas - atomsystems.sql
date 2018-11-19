
-- forma de pago x turno y nro de caja (otros)

SELECT forma_pago.nom_librador, SUM(forma_pago.monto_pago) AS total_venta
FROM venta_cab
INNER JOIN forma_pago
ON venta_cab.nro_ticket = forma_pago.nro_ticket
WHERE venta_cab.nro_turno = 9 AND venta_cab.cod_caja = 16 AND forma_pago.tipo_cuenta <> 'CRE' AND forma_pago.tipo_cuenta <> 'EFE'
AND forma_pago.nro_turno = 9 AND forma_pago.cod_caja = 16
GROUP BY forma_pago.nom_librador

-- VENTAS: forma de pago 
---------------------------------------------------------------
-- CAMPOS: Forma de pago | Total | Tipo de Cambio | Total Gs. |
---------------------------------------------------------------

SELECT forma_pago.nom_librador, SUM(forma_pago.monto_pago) AS total_cobro,  
forma_pago.tip_cambio, (sum(forma_pago.monto_pago) * forma_pago.tip_cambio) as total_cambio, 
CASE WHEN forma_pago.tipo_cuenta = 'CRE' THEN cuenta.cod_cliente ELSE forma_pago.cod_cuenta END as codigo
FROM forma_pago
INNER JOIN cuenta
ON forma_pago.cod_cuenta = cuenta.cod_cuenta
WHERE forma_pago.fec_cobro::date >= '05/09/2018'::date and forma_pago.fec_cobro::date <= '06/09/2018'
GROUP BY forma_pago.nom_librador, forma_pago.cod_cuenta, forma_pago.tip_cambio, forma_pago.tipo_cuenta, cuenta.cod_cliente
ORDER BY forma_pago.cod_cuenta 

-- VENTAS: por caja 
-----------------------------------------------------------------------------------------------
-- CAMPOS: | Caja | Descripcion | Monto costo s/ IVA | IVA Costo | Total Vta | Utilidad Bruta |
-----------------------------------------------------------------------------------------------
-- TABLAS: venta_cab, forma_pago, cuenta, articulo

SELECT DISTINCT cod_caja, ('CAJA - ' || cod_caja) AS descripcion, SUM(mon_descuento) as descuento, 
SUM(mon_costo) AS costo, SUM(mon_venta) AS total_venta, 
(SUM(mon_venta) - SUM(mon_costo)) AS utilidad
FROM venta_det
WHERE fec_comprob::date >= '05/09/2018'::date AND fec_comprob::date <= '05/09/2018'::date
AND estado = 'V'
GROUP BY cod_caja


-- VENTAS: por grupo de articulos
---------------------------------------------------------------------------
-- CAMPOS: | Código | Descripcion | Costo IVA Inc | Total Vta | Utilidad |
---------------------------------------------------------------------------

SELECT DISTINCT grupo.cod_grupo AS codigo, grupo.descripcion AS descripcion, 
SUM(venta_det.mon_costo) AS costo, SUM(venta_det.mon_venta) AS total_venta, 
(SUM(venta_det.mon_venta) - SUM(venta_det.mon_costo)) AS utilidad
FROM venta_det
INNER JOIN articulo 
ON venta_det.cod_articulo = articulo.cod_articulo 
INNER JOIN grupo 
ON grupo.cod_grupo = articulo.cod_grupo 
WHERE venta_det.fec_comprob::date >= '05/09/2018'::date AND venta_det.fec_comprob::date <= '06/09/2018'::date
AND venta_det.estado = 'V' 
GROUP BY grupo.cod_grupo







