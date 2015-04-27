package router;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Router {

    public static void main(String[] args) throws FileNotFoundException {
        try {
            Scanner filename = new Scanner(System.in);
            System.out.println("enter router filename");
            String file1 = filename.next();
            System.out.println("enter packet filename");
            String file2 = filename.next();
            /* String file1 = "RoutingTable.txt";
             String file2 = "packets.txt";       */
            File f1 = new File(file1);
            Scanner s1 = new Scanner(f1);
            File f2 = new File(file2);
            String defa = getdefault(f1);
            String destIP, routerline, line = "";

            Scanner packetreader = new Scanner(f2);

            while (packetreader.hasNextLine()) {
                destIP = packetreader.nextLine();
                System.out.print("\n packet with destination address " + destIP + " will be forwarded to ");
                Scanner routerreader = new Scanner(f1);
                line = "";

                while (routerreader.hasNextLine()) {
                    routerline = routerreader.nextLine();
                    String[] parts = routerline.split(" ");
                    String dest = getdestination(parts[0], destIP);
                    if (routerline.contains(dest)) {
                        line = line + routerline + ":";
                    }
                }                                                                       // router reader ends

                String finalline = getline(line);
                String[] linetonexthop = linetonexthop(finalline);
                if (finalline != "") {
                    if (linetonexthop[3].contains("Direct")) {
                        System.out.print(linetonexthop[1] + " out on interface " + linetonexthop[4]);
                    } else {
                        System.out.print(linetonexthop[2] + " out on interface " + linetonexthop[4]);
                    }

                } else if (finalline == "") {
                    linetonexthop = linetonexthop(defa);
                    System.out.print(linetonexthop[2] + " out on interface " + linetonexthop[4]);

                }
            }                                                                           //packetreader ends
        } catch (FileNotFoundException e) {
            System.out.println("Either one or both file names are not correct");
        }
        System.out.println("");
    }                                                                               //main ends

    private static String getdestination(String mask, String destIP) {              //does the and operation between packet dest and mask
        String nw = "";
        String[] IP = destIP.split("\\.");
        String[] maskpart = mask.split("\\.");
        for (int i = 0; i < 4; i++) {
            int a = Integer.parseInt(IP[i]);
            int b = Integer.parseInt(maskpart[i]);
            int c = a & b;
            if (i < 3) {
                nw = nw + c + ".";
            } else {
                nw = nw + c;
            }
        }
        return nw;
    }

    private static String getline(String line) {                                    //finds the most appropriate router rule
        String saveline = "";
        String[] line1 = line.split("\\:");
        for (String a : line1) {
            int count = 0, pcount = 0, zc = 0;
            String[] b = a.split(" ");
            String[] c = b[0].split("\\.");
            for (String x : c) {
                if (x.contains("255")) {
                    count++;
                } else {
                    zc++;
                }
            }
            if ((count > pcount) && (zc != 4)) {
                saveline = a;
                pcount = count;
            }
        }
        return saveline;
    }

    private static String getdefault(File f1) throws FileNotFoundException {        //gets default rule
        String d = "", theline = "";
        Scanner def = new Scanner(f1);
        while (def.hasNextLine()) {
            d = def.nextLine();
            if (d.contains("Default")) {
                theline = d;
                break;
            }
        }
        return theline;
    }

    private static String[] linetonexthop(String finalline) {                       //splits individual router lines into arrays
        String[] line = finalline.split("\\ ");
        return line;
    }
}
