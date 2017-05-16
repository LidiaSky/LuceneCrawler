import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;
import robot.RobotLauncher;

import java.io.IOException;
import java.net.InetAddress;


public class Server {
   /* public static void main(String [] args)
    {
        RobotLauncher launcher = new RobotLauncher();
        System.out.println(launcher.Search("science"));
    }                                              */
    private static final int port = 4444;

    public static void main(String args[]) throws IOException, XmlRpcException {
        // Запускаем веб-сервер на указанном порту.
        System.out.println("Let's start the server!");
        try
        {
            WebServer webServer = new WebServer(port);
            // Запускаем на веб-сервере сервер XMLRPC
            XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
            PropertyHandlerMapping phm = new PropertyHandlerMapping();
            // Добавляем обработчик запросов - класс RobotLauncher
            phm.addHandler("RobotLauncher",RobotLauncher.class);

            xmlRpcServer.setHandlerMapping(phm);

            XmlRpcServerConfigImpl serverConfig =
                    (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
            serverConfig.setEnabledForExtensions(true);
            serverConfig.setContentLengthOptional(false);
            // Запускаем веб-сервер.
            webServer.start();
            RobotLauncher search = new RobotLauncher();
            search.Search("ad hoc");
            //InetAddress ip;
            //ip = InetAddress.getLocalHost();
            //System.out.println("Current IP address : " + ip.getHostAddress());


        }
        catch(Exception exc)
        {
            System.out.println("Exception occured!");
        }
        System.out.println("Server started!");
    }
}
