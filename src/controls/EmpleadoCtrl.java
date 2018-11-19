/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controls;

import beans.EmpleadoBean;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import utiles.DBManager;
import utiles.InfoErrores;

/**
 *
 * @author Andres
 */
public class EmpleadoCtrl {
    
    PreparedStatement pstmt;
    ResultSet rs;
    
    String buscaMaxCodEmpleado = "SELECT MAX(cod_empleado) FROM empleado";
    
    String buscaEmpleadoDescripcion = "SELECT cod_empleado, nombre, apellido, cip_empleado, activo, to_char(fec_vigencia, 'dd/MM/yyyy') as fecVigencia, "
                                       + "cod_usuario, cod_seccion, sexo, direccion, cod_barrio, cod_ciudad, telefono_fijo, telefono_movil, nro_aseg_ips, "
                                       + "can_hijos, to_char(fec_ingreso, 'dd/MM/yyyy') AS fecIngreso, recomendacion, cod_horario, hora_extra, estado_civil, sueldo1, sueldo2, nacionalidad, "
                                       + "lugar_nacimiento, nombre_conyuge, email, to_char(fec_nacimiento, 'dd/MM/yyyy') AS fecNacimiento, figura_planilla, permite_marcacion, horas_extras_aut, "
                                       + "descuenta_ips, cod_local, nivel_academico, obs_nivel_academico, cod_nacionalidad, to_char(fec_egreso, 'dd/MM/yyyy') AS fecEgreso, pct_comision, "
                                       + "cod_profesion, cod_usuario_sistema, cod_cargo, motivo_salida, to_char(fec_ingreso_ips, 'dd/MM/yyy') AS fecIngresoIps, "
                                       + "path_img "
                                       + "FROM empleado "
                                       + "WHERE nombre LIKE ? "
                                       + "ORDER BY nombre";
    
    String getNombreEmpleado = "SELECT nombre || ' ' || apellido AS nombre FROM empleado WHERE cod_empleado = ?";
    
    String buscaEmpleadoCodigo = "SELECT cod_empleado, nombre, apellido, cip_empleado, activo, to_char(fec_vigencia, 'dd/MM/yyyy') AS fecVigencia, "
                                  + "cod_usuario, cod_seccion, sexo, direccion, cod_barrio, cod_ciudad, telefono_fijo, telefono_movil, nro_aseg_ips, "
                                  + "can_hijos, to_char(fec_ingreso, 'dd/MM/yyyy') AS fecIngreso, recomendacion, cod_horario, hora_extra, estado_civil, sueldo1, sueldo2, nacionalidad, "
                                  + "lugar_nacimiento, nombre_conyuge, email, to_char(fec_nacimiento, 'dd/MM/yyyy') as fecNacimiento, figura_planilla, permite_marcacion, horas_extras_aut, "
                                  + "descuenta_ips, cod_local, nivel_academico, obs_nivel_academico, cod_nacionalidad, to_char(fec_egreso, 'dd/MM/yyyy') AS fecEgreso, pct_comision,"
                                  + "cod_profesion, cod_usuario_sistema, cod_cargo, motivo_salida, to_char(fec_ingreso_ips, 'dd/MM/yyy') AS fecIngresoIps, path_img "
                                  + "FROM empleado "
                                  + "WHERE cod_empleado = ? ";
    
    String catastraEmpleado = "INSERT INTO empleado (cod_empleado, nombre, apellido, cip_empleado, activo, fec_vigencia, cod_usuario, cod_seccion, "
                            + "sexo, direccion, cod_barrio, cod_ciudad, telefono_fijo, telefono_movil, nro_aseg_ips, can_hijos, fec_ingreso, "
                            + "recomendacion, cod_horario, hora_extra, estado_civil, sueldo1, sueldo2, nacionalidad, lugar_nacimiento, nombre_conyuge, "
                            + "email, fec_nacimiento, figura_planilla, permite_marcacion, horas_extras_aut, descuenta_ips, cod_local, nivel_academico, "
                            + "obs_nivel_academico, cod_nacionalidad, fec_egreso, pct_comision, cod_profesion, cod_usuario_sistema, cod_cargo, motivo_salida, "
                            + "fec_ingreso_ips) "
                            + "VALUES (?, ?, ?, ?, ?, 'now()', ?, ?, "
                                    + "?, ?, ?, ?, ?, ?, ?, ?, to_date(?, 'dd/MM/yyyy'), "
                                    + "?, ?, ?, ?, ?, ?, ?, ?, ?, "
                                    + "?, to_date(?, 'dd/MM/yyyy'), ?, ?, ?, ?, ?, ?, "
                                    + "?, ?, to_date(?, 'dd/MM/yyyy'), ?, ?, ?, ?, ?, "
                                    + "to_date(?, 'dd/MM/yyyy'))";
    
    String modificaEmpleado = "UPDATE empleado SET nombre = ?, apellido = ?, cip_empleado = ?, activo = ?, fec_vigencia = 'now()', cod_usuario = ?, cod_seccion = ?, "
                            + "sexo = ?, direccion = ?, cod_barrio = ?, cod_ciudad = ?, telefono_fijo = ?, telefono_movil = ?, "
                            + "nro_aseg_ips = ?, can_hijos = ?, fec_ingreso = to_date(?, 'dd/MM/yyyy'), recomendacion = ?, cod_horario = ?, hora_extra = ?, estado_civil = ?, "
                            + "sueldo1 = ?, sueldo2 = ?, nacionalidad = ?, lugar_nacimiento = ?, nombre_conyuge = ?, email = ?, fec_nacimiento = to_date(?, 'dd/MM/yyyy'), "
                            + "figura_planilla = ?, permite_marcacion = ?, horas_extras_aut = ?, descuenta_ips = ?, cod_local = ?, nivel_academico = ?, "
                            + "obs_nivel_academico = ?, cod_nacionalidad = ?, fec_egreso = to_date(?, 'dd/MM/yyyy'), pct_comision = ?, cod_profesion = ?, "
                            + "cod_usuario_sistema = ?, cod_cargo = ?, motivo_salida = ?, fec_ingreso_ips = to_date(?, 'dd/MM/yyyy') "
                            + "WHERE cod_empleado = ?";
    
    public EmpleadoCtrl(){}
    
    public int buscaMaxCodEmpleado(){
        int result = 0;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(buscaMaxCodEmpleado);
            rs = pstmt.executeQuery();
            if(rs.next()){
                result = rs.getInt(1);
            }
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
            InfoErrores.errores(sqlex);
        }finally{
            try{
                rs.close();
            }catch(SQLException sqlex){
                sqlex.printStackTrace();
            }
        }
        return result;
    }
    
    public boolean modificaEmpleado(EmpleadoBean empleado){
        try{
            pstmt = DBManager.conn.prepareStatement(modificaEmpleado);
            pstmt.setString( 1, empleado.getNombre());
            pstmt.setString( 2, empleado.getApellido());
            pstmt.setString( 3, empleado.getCipEmpleado());
            pstmt.setString( 4, empleado.getActivo());
            pstmt.setInt(    5, empleado.getCodUsuario());
            pstmt.setInt(    6, empleado.getCodSeccion());
            pstmt.setString( 7, empleado.getSexo());
            pstmt.setString( 8, empleado.getDireccion());
            pstmt.setInt(    9, empleado.getCodBarrio());
            pstmt.setInt(   10, empleado.getCodCiudad());
            pstmt.setString(11, empleado.getTelefonoFijo());
            pstmt.setString(12, empleado.getTelefonoMovil());
            pstmt.setString(13, empleado.getNroAsegIps()); //
            pstmt.setInt(   14, empleado.getCanHijos());
            pstmt.setString(15, empleado.getFecIngreso());
            pstmt.setString(16, empleado.getRecomendacion());
            pstmt.setString(17, empleado.getCodHorario()); //
            pstmt.setString(18, empleado.getHoraExtra());
            pstmt.setString(19, empleado.getEstadoCivil());
            pstmt.setInt(   20, empleado.getSueldo1());
            pstmt.setInt(   21, empleado.getSueldo2());
            pstmt.setString(22, empleado.getNacionalidad());
            pstmt.setString(23, empleado.getLugarNacimiento());
            pstmt.setString(24, empleado.getNombreConyugue());
            pstmt.setString(25, empleado.getEmail());
            pstmt.setString(26, empleado.getFecNacimiento());
            pstmt.setString(27, empleado.getFiguraPlanilla());
            pstmt.setString(28, empleado.getPermiteMarcacion());
            pstmt.setInt(   29, empleado.getHorasExtrasAut());
            pstmt.setString(30, empleado.getDescuentaIps());
            pstmt.setInt(   31, empleado.getCodLocal());
            pstmt.setString(32, empleado.getNivelAcademico());
            pstmt.setString(33, empleado.getObsNivelAcademico());
            pstmt.setInt(   34, empleado.getCodNacionalidad());
            pstmt.setString(35, empleado.getFecEgreso());
            pstmt.setDouble(36, empleado.getPctComision());
            pstmt.setInt(   37, empleado.getCodProfesion());
            pstmt.setInt(   38, empleado.getCodUsuarioSistema());
            pstmt.setInt(   39, empleado.getCodCargo());
            pstmt.setString(40, empleado.getMotivoSalida());
            pstmt.setString(41, empleado.getFecIngresoIps());
            pstmt.setInt(   42, empleado.getCodEmpleado()); // WHERE
            
            if(pstmt.executeUpdate() > 0){
                DBManager.conn.commit();
                return true;
            }else{
                DBManager.conn.rollback();
                return false;
            }
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
    
    public boolean catastraEmpleado(EmpleadoBean empleado){
        try{
            pstmt = DBManager.conn.prepareStatement(catastraEmpleado);
            pstmt.setInt(    1, empleado.getCodEmpleado()); 
            pstmt.setString( 2, empleado.getNombre());
            pstmt.setString( 3, empleado.getApellido());
            pstmt.setString( 4, empleado.getCipEmpleado());
            pstmt.setString( 5, empleado.getActivo());
            pstmt.setInt(    6, empleado.getCodUsuario());
            pstmt.setInt(    7, empleado.getCodSeccion());
            pstmt.setString( 8, empleado.getSexo());
            pstmt.setString( 9, empleado.getDireccion());
            pstmt.setInt(   10, empleado.getCodBarrio());
            pstmt.setInt(   11, empleado.getCodCiudad());
            pstmt.setString(12, empleado.getTelefonoFijo());
            pstmt.setString(13, empleado.getTelefonoMovil());
            pstmt.setString(14, empleado.getNroAsegIps()); //
            pstmt.setInt(   15, empleado.getCanHijos());
            pstmt.setString(16, empleado.getFecIngreso());
            pstmt.setString(17, empleado.getRecomendacion());
            pstmt.setString(18, empleado.getCodHorario()); //
            pstmt.setString(19, empleado.getHoraExtra());
            pstmt.setString(20, empleado.getEstadoCivil());
            pstmt.setInt(   21, empleado.getSueldo1());
            pstmt.setInt(   22, empleado.getSueldo2());
            pstmt.setString(23, empleado.getNacionalidad());
            pstmt.setString(24, empleado.getLugarNacimiento());
            pstmt.setString(25, empleado.getNombreConyugue());
            pstmt.setString(26, empleado.getEmail());
            pstmt.setString(27, empleado.getFecNacimiento());
            pstmt.setString(28, empleado.getFiguraPlanilla());
            pstmt.setString(29, empleado.getPermiteMarcacion());
            pstmt.setInt(   30, empleado.getHorasExtrasAut());
            pstmt.setString(31, empleado.getDescuentaIps());
            pstmt.setInt(   32, empleado.getCodLocal());
            pstmt.setString(33, empleado.getNivelAcademico());
            pstmt.setString(34, empleado.getObsNivelAcademico());
            pstmt.setInt(   35, empleado.getCodNacionalidad());
            pstmt.setString(36, empleado.getFecEgreso());
            pstmt.setDouble(37, empleado.getPctComision());
            pstmt.setInt(   38, empleado.getCodProfesion());
            pstmt.setInt(   39, empleado.getCodUsuarioSistema());
            pstmt.setInt(   40, empleado.getCodCargo());
            pstmt.setString(41, empleado.getMotivoSalida());
            pstmt.setString(42, empleado.getFecIngresoIps());
            
            
            if(pstmt.executeUpdate() > 0){
                DBManager.conn.commit();
                return true;
            }else{
                DBManager.conn.rollback();
                return false;
            }
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
    
    public List<EmpleadoBean> buscaEmpleadoDescripcion(String descripcion){
        List<EmpleadoBean> empleadoList = new ArrayList();
        ResultSet rs = null;
        
        try{
            pstmt = DBManager.conn.prepareStatement(buscaEmpleadoDescripcion);
            pstmt.setString(1, descripcion);
            rs = pstmt.executeQuery();
            EmpleadoBean empleadoBean = null;
            while(rs.next()){
                empleadoBean = new EmpleadoBean();
                empleadoBean.setActivo(rs.getString("activo"));
                empleadoBean.setApellido(rs.getString("apellido"));
                empleadoBean.setCanHijos(rs.getInt("can_hijos"));
                empleadoBean.setCipEmpleado(rs.getString("cip_empleado"));
                empleadoBean.setCodBarrio(rs.getInt("cod_barrio"));
                empleadoBean.setCodCiudad(rs.getInt("cod_ciudad"));
                empleadoBean.setCodEmpleado(rs.getInt("cod_empleado"));
                empleadoBean.setCodHorario(rs.getString("cod_horario"));
                empleadoBean.setCodLocal(rs.getInt("cod_local"));
                empleadoBean.setCodNacionalidad(rs.getInt("cod_nacionalidad"));
                empleadoBean.setCodSeccion(rs.getInt("cod_seccion"));
                empleadoBean.setCodUsuario(rs.getInt("cod_usuario"));
                empleadoBean.setDescuentaIps(rs.getString("descuenta_ips"));
                empleadoBean.setDireccion(rs.getString("direccion"));
                empleadoBean.setEmail(rs.getString(rs.getString("email")));
                empleadoBean.setEstadoCivil(rs.getString("estado_civil"));
                empleadoBean.setFecEgreso(rs.getString("fecEgreso"));
                empleadoBean.setFecIngreso(rs.getString("fecIngreso"));
                empleadoBean.setFecNacimiento(rs.getString("fecNacimiento"));
                empleadoBean.setFecVigencia(rs.getString("fecVigencia"));
                empleadoBean.setFiguraPlanilla(rs.getString("figura_planilla"));
                empleadoBean.setHoraExtra(rs.getString("hora_extra"));
                empleadoBean.setHorasExtrasAut(rs.getInt("horas_extras_aut"));
                empleadoBean.setLugarNacimiento(rs.getString("lugar_nacimiento"));
                empleadoBean.setNacionalidad(rs.getString("nacionalidad"));
                empleadoBean.setNivelAcademico(rs.getString("nivel_academico"));
                empleadoBean.setNombre(rs.getString("nombre"));
                empleadoBean.setNombreConyugue(rs.getString("nombre_conyuge"));
                empleadoBean.setNroAsegIps(rs.getString("nro_aseg_ips"));
                empleadoBean.setObsNivelAcademico(rs.getString("obs_nivel_academico"));
                empleadoBean.setPctComision(rs.getDouble("pct_comision"));
                empleadoBean.setPermiteMarcacion(rs.getString("permite_marcacion"));
                empleadoBean.setRecomendacion(rs.getString("recomendacion"));
                empleadoBean.setSexo(rs.getString("sexo"));
                empleadoBean.setSueldo1(rs.getInt("sueldo1"));
                empleadoBean.setSueldo2(rs.getInt("sueldo2"));
                empleadoBean.setTelefonoFijo(rs.getString("telefono_fijo"));
                empleadoBean.setTelefonoMovil(rs.getString("telefono_movil"));
                empleadoBean.setCodProfesion(rs.getInt("cod_profesion"));
                empleadoBean.setCodUsuarioSistema(rs.getInt("cod_usuario_sistema"));
                empleadoBean.setCodCargo(rs.getInt("cod_cargo"));
                empleadoBean.setMotivoSalida(rs.getString("motivo_salida"));
                empleadoBean.setFecIngresoIps(rs.getString("fecIngresoIps"));
                empleadoBean.setPathImg(rs.getString("path_img"));
                empleadoList.add(empleadoBean);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            try{
                rs.close();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return empleadoList;
    }
    
    public EmpleadoBean buscaEmpleadoCodigo(int codigo){
        EmpleadoBean bean = null;
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(buscaEmpleadoCodigo);
            pstmt.setInt(1, codigo);
            rs = pstmt.executeQuery();
            while(rs.next()){
                bean = new EmpleadoBean();
                bean.setActivo(rs.getString("activo"));
                bean.setApellido(rs.getString("apellido"));
                bean.setCanHijos(rs.getInt("can_hijos"));
                bean.setCipEmpleado(rs.getString("cip_empleado"));
                bean.setCodBarrio(rs.getInt("cod_barrio"));
                bean.setCodCiudad(rs.getInt("cod_ciudad"));
                bean.setCodEmpleado(rs.getInt("cod_empleado"));
                bean.setCodHorario(rs.getString("cod_horario"));
                bean.setCodLocal(rs.getInt("cod_local"));
                bean.setCodNacionalidad(rs.getInt("cod_nacionalidad"));
                bean.setCodSeccion(rs.getInt("cod_seccion"));
                bean.setCodUsuario(rs.getInt("cod_usuario"));
                bean.setDescuentaIps(rs.getString("descuenta_ips"));
                bean.setDireccion(rs.getString("direccion"));
                bean.setEmail(rs.getString("email"));
                bean.setEstadoCivil(rs.getString("estado_civil"));
                bean.setFecEgreso(rs.getString("fecEgreso"));
                bean.setFecIngreso(rs.getString("fecIngreso"));
                bean.setFecNacimiento(rs.getString("fecNacimiento"));
                bean.setFecVigencia(rs.getString("fecVigencia"));
                bean.setFiguraPlanilla(rs.getString("figura_planilla"));
                bean.setHoraExtra(rs.getString("hora_extra"));
                bean.setHorasExtrasAut(rs.getInt("horas_extras_aut"));
                bean.setLugarNacimiento(rs.getString("lugar_nacimiento"));
                bean.setNacionalidad(rs.getString("nacionalidad"));
                bean.setNivelAcademico(rs.getString("nivel_academico"));
                bean.setNombre(rs.getString("nombre"));
                bean.setNombreConyugue(rs.getString("nombre_conyuge"));
                bean.setNroAsegIps(rs.getString("nro_aseg_ips"));
                bean.setObsNivelAcademico(rs.getString("obs_nivel_academico"));
                bean.setPctComision(rs.getDouble("pct_comision"));
                bean.setPermiteMarcacion(rs.getString("permite_marcacion"));
                bean.setRecomendacion(rs.getString("recomendacion"));
                bean.setSexo(rs.getString("sexo"));
                bean.setSueldo1(rs.getInt("sueldo1"));
                bean.setSueldo2(rs.getInt("sueldo2"));
                bean.setTelefonoFijo(rs.getString("telefono_fijo"));
                bean.setTelefonoMovil(rs.getString("telefono_movil"));
                bean.setCodProfesion(rs.getInt("cod_profesion"));
                bean.setCodUsuarioSistema(rs.getInt("cod_usuario_sistema"));
                bean.setCodCargo(rs.getInt("cod_cargo"));
                bean.setMotivoSalida(rs.getString("motivo_salida"));
                bean.setFecIngresoIps(rs.getString("fecIngresoIps"));
                bean.setPathImg(rs.getString("path_img"));
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            try{
                rs.close();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        
        return bean;
    }
    
    
    public String getNombreEmpleado(int codigo){
        String result = "INEXISTENTE";
        ResultSet rs = null;
        try{
            pstmt = DBManager.conn.prepareStatement(getNombreEmpleado);
            pstmt.setInt(1, codigo);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                result = rs.getString("nombre");
            }
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch (SQLException sex) {
                sex.printStackTrace();
            }
        }
        return result;
    }
}
