package com.example.controldeinventario;

import java.sql.*;
public class Conexion {
    private String servidor="localhost";
    private String usuario="root";
    private String password="";
    private String bd="inventario";
    public Connection conexion;

    public Conexion(){
        try{
            conexion=DriverManager.getConnection("jdbc:mysql://"+servidor+":3306/"+bd+"?useUnicode=true&useJDBCCompilantTimeZoneShift=useLegacyDatetimeCode&serverTimeZone=UTC",usuario,password);




        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }//constructor



    //   C    O   N   S   U   L   T   A   R
    public ResultSet consultar(String consulta, Object... datos){
        ResultSet resultado=null;
        try{
            Statement st=conexion.createStatement();
            PreparedStatement pst = conexion.prepareStatement(consulta);
            if (datos.length > 0){
                for (int i=1; i<= datos.length;i++){
                    pst.setObject(i,datos[i-1]);
                    System.out.println(datos[i-1]);
                }
                resultado = pst.executeQuery();
            }else {
                resultado =st.executeQuery(consulta);
            }


        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return resultado;
    }
    // insertar, modificar o eliminar
    public void insmodelim(String sql, Object... datos){
        try{
            Statement st=conexion.createStatement();
            PreparedStatement pst = conexion.prepareStatement(sql);
            if (datos.length > 0){
                for (int i=1;i<=datos.length;i++){
                    pst.setObject(i,datos[i-1]);
                }
                pst.executeUpdate();
            }else {
                st.executeUpdate(sql);
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
