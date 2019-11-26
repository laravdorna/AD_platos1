/*
 Debes desenvolver unha aplicacion que partindo do ficheiro platoss e das taboas
 dadas cree un ficheiro XML chamado  totalgraxas.xml cuxo contido será o código 
 e nome de cada plato referido  no ficheiro platoss   xunto co seu contido en graxas totais,
 ( Ter en conta que ainda que na taboa composicion temos os   componentes de tres
 platos  so deben grabarse  o contido en graxa dos pratos p1 e p2 que son   os que 
 estan referidos no ficheiro platoss. E decir o ficheiro platoss conten a referencia
 dos obxectos cuxos componentes debemos buscar para facer os calculos pertinentes)
 */
/* COMANDOS
lanzar servidor oracle para trabajar desde java con el listener

. oraenv
 orcl
rlwrap sqlplus sys/oracle as sysdba 
startup
conn hr/hr
exit
lsnrctl start
lsnrctl status

*/
package exa15;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 *
 * @author oracle
 */
public class Exa15 {

    public static Connection conexion = null;

    public static Connection getConexion() throws SQLException {
        String usuario = "hr";
        String password = "hr";
        String host = "localhost";
        String puerto = "1521";
        String sid = "orcl";
        String ulrjdbc = "jdbc:oracle:thin:" + usuario + "/" + password + "@" + host + ":" + puerto + ":" + sid;

        conexion = DriverManager.getConnection(ulrjdbc);
        return conexion;
    }

    public static void closeConexion() throws SQLException {
        conexion.close();
    }

    public static void main(String[] args) throws FileNotFoundException, IOException, SQLException, ClassNotFoundException, XMLStreamException {

        //establecer conexion
        Exa15.getConexion();

        // escribir los datos en un fichero xml de forma automatica
        File fichero = new File("/home/oracle/Desktop/compartido/exa15/totalgraxas.xml");
        FileWriter fichEscribir = new FileWriter(fichero);
        XMLOutputFactory factor = XMLOutputFactory.newInstance();
        XMLStreamWriter escribirXML = factor.createXMLStreamWriter(fichEscribir);

        //CREAR OBJETO PLATO
        Platos p = new Platos();

        //leer un metodo serializado
        String textoRuta = "/home/oracle/Desktop/compartido/exa15/platoss";
        ObjectInputStream leerO = new ObjectInputStream(new FileInputStream(textoRuta));

        /*
         1 p - deben amosar  os valores correspondentes a cada plato 
         (e dicir o seu codigo e nome do plato , que se len do ficheiro platoss). 
         */
        Statement stm = conexion.createStatement();

        //escribe a declaracion XML con a Version especificada y la etiqueta raiz
        escribirXML.writeStartDocument("1.0");
        escribirXML.writeStartElement("Platos");

        while ((p = (Platos) leerO.readObject()) != null) {
            System.out.println(p.toString());
            
            //empezar etiqueta del plato y su codigo y el nombre
            escribirXML.writeStartElement("Plato");
            escribirXML.writeAttribute("Codigop", p.getCodigop());
            escribirXML.writeStartElement("nombrep");
            escribirXML.writeCharacters(p.getNomep());
            escribirXML.writeEndElement();
            ResultSet rs = stm.executeQuery("select * from composicion where CODP = '" + p.getCodigop() + "' ");

            //variable que guarda el codigocomponenete
            int grasatotal = 0;
            while (rs.next()) {
                //atributos rs  
                String codcom = rs.getString(2);
                int pesocom = rs.getInt(3);

                System.out.println("\tcodigo componenete: " + codcom);
                //buscar query en la tabla componentes
                Statement stm2 = conexion.createStatement();
                ResultSet rs2 = stm2.executeQuery("select * from componentes where CODC = '" + codcom + "' ");
                rs2.next();
                System.out.println("\tgrasa por cada 100g=" + rs2.getInt(3));
                System.out.println("\tpeso: " + pesocom);
                //atributos rs2
                int grasa = rs2.getInt(3);
                int grasatotalcomp = pesocom * grasa / 100;

                grasatotal += grasatotalcomp;
                System.out.println("\tTotal grasa del componente= " + grasatotalcomp + "\n");

            }
            System.out.println("TOTAL GRASA EN EL PLATO= " + grasatotal + ".\n");
            
            escribirXML.writeStartElement("grasaTotal");
            escribirXML.writeCharacters(Integer.toString(grasatotal));
            escribirXML.writeEndElement();
            escribirXML.writeEndElement();//cerrar etiqueta plato
        }
        escribirXML.writeEndElement();//cerrar etiqueta raiz
        escribirXML.writeEndDocument();//cerrar etiqueta xml

        escribirXML.close();
        fichEscribir.close();
        leerO.close();

        /*        
         1 p – debes amosar cada un dos codigos de componente que compoñen un plato
         e o seu peso no plato   (que se len da taboa composicion)
         */
        /*
         //acceder a la tabla composicion --probamos dando el cod =p1 directamente
         Statement stm = conexion.createStatement();
         ResultSet rs = stm.executeQuery("select * from composicion where CODP = 'p1' ");
         
         //"crea lista de tabla"
         System.out.println("CODP\tCODC\tPESO");
         
         while (rs.next()) {

         System.out.println(rs.getString(1) + "\t" + rs.getString(2) + "\t" + rs.getInt(3));

         }
         */
        /*
         0,6 p -  debes amosar o total parcial de graxa correspondente a cada 
         componente do plato ( que se obten multiplicando a graxa do componente, 
         multiplicandoa polo seu peso no prato e dividindoa entre 100)
         */
        /*
         0,4 p – debes amosar o total en graxas do plato ( que se obten sumando os totais
         parciais obtidos anteiormente para un mesmo plato)
         */
        /*
         1 p – debe xerarse un ficheiro XML chamado totalgraxas.xml  co codigo e nome 
         de cada plato do ficheiro serializado platoss xunto   co seu contido en graxas totais,
         (para comprobar que se fixo ben debe executarse dito ficheiro cun doble click e 
         veremos no navegador o codigo xml amosado anterirormente)
         */
        //cerrar conexion 
        Exa15.getConexion();

    }

}
