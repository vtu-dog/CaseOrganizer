package app.main;

import app.ftpconn.FTPConn;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            FTPConn conn = new FTPConn("localhost", "admin", "admin");
            conn.readRootMetadata();
            conn.closeConn();
        } catch (Exception e) { System.out.println(e); }
    }
}
