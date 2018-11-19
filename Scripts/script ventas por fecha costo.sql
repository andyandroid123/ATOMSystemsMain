select * from venta_det order  by fec_vigencia desc limit 8

select * from turno where nro_turno = (select max(nro_turno) from turno where cod_caja =16)

select * from venta_cab where nro_turno = 1 and cod_caja = 16

select * from venta_det where nro_ticket = 58

select venta_det.cod_articulo, venta_det.mon_costo,  venta_det.mon_venta
from venta_det 
join venta_cab
on venta_det.nro_ticket = venta_cab.nro_ticket 
where venta_cab.nro_turno = 1 and venta_cab.cod_caja = 15 and venta_det.nro_turno = 1