/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webcache;

/**
 *
 * @author Vinicius
 */
/**
Copyright (C) 2004  Juho Vähä-Herttua

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/


import java.io.*;
import java.util.*;
import java.text.*;
import java.net.URLDecoder;

public class HttpParser {
  private static final String[][] HttpReplies = {{"100", "Continue"},
                                                 {"101", "Switching Protocols"},
                                                 {"200", "OK"},
                                                 {"201", "Created"},
                                                 {"202", "Accepted"},
                                                 {"203", "Non-Authoritative Information"},
                                                 {"204", "No Content"},
                                                 {"205", "Reset Content"},
                                                 {"206", "Partial Content"},
                                                 {"300", "Multiple Choices"},
                                                 {"301", "Moved Permanently"},
                                                 {"302", "Found"},
                                                 {"303", "See Other"},
                                                 {"304", "Not Modified"},
                                                 {"305", "Use Proxy"},
                                                 {"306", "(Unused)"},
                                                 {"307", "Temporary Redirect"},
                                                 {"400", "Bad Request"},
                                                 {"401", "Unauthorized"},
                                                 {"402", "Payment Required"},
                                                 {"403", "Forbidden"},
                                                 {"404", "Not Found"},
                                                 {"405", "Method Not Allowed"},
                                                 {"406", "Not Acceptable"},
                                                 {"407", "Proxy Authentication Required"},
                                                 {"408", "Request Timeout"},
                                                 {"409", "Conflict"},
                                                 {"410", "Gone"},
                                                 {"411", "Length Required"},
                                                 {"412", "Precondition Failed"},
                                                 {"413", "Request Entity Too Large"},
                                                 {"414", "Request-URI Too Long"},
                                                 {"415", "Unsupported Media Type"},
                                                 {"416", "Requested Range Not Satisfiable"},
                                                 {"417", "Expectation Failed"},
                                                 {"500", "Internal Server Error"},
                                                 {"501", "Not Implemented"},
                                                 {"502", "Bad Gateway"},
                                                 {"503", "Service Unavailable"},
                                                 {"504", "Gateway Timeout"},
                                                 {"505", "HTTP Version Not Supported"}};

  private BufferedReader reader;
  private String method, url;
  private Hashtable headers, params;
  private int[] ver;

  public HttpParser(InputStream is) {
    reader = new BufferedReader(new InputStreamReader(is));
    method = "";
    url = "";
    headers = new Hashtable();
    params = new Hashtable();
    ver = new int[2];
  }

  public String parseGetHost(String get) throws IOException {
    String host = new String();
    String temp[] = new String[1000];
    temp = get.split("\\s");
    if(temp.length < 2)
    {
        host = "";
    }
    else
    {
    host = temp[1];
    }  
    return host;
  }

  private void parseHeaders() throws IOException {
    String line;
    int idx;

    // that fscking rfc822 allows multiple lines, we don't care now
    line = reader.readLine();
    while (!line.equals("")) {
      idx = line.indexOf(':');
      if (idx < 0) {
        headers = null;
        break;
      }
      else {
        headers.put(line.substring(0, idx).toLowerCase(), line.substring(idx+1).trim());
      }
      line = reader.readLine();
    }
  }

  public String getMethod() {
    return method;
  }

  public String getHeader(String key) {
    if (headers != null)
      return (String) headers.get(key.toLowerCase());
    else return null;
  }

  public Hashtable getHeaders() {
    return headers;
  }

  public String getRequestURL() {
    return url;
  }

  public String getParam(String key) {
    return (String) params.get(key);
  }

  public Hashtable getParams() {
    return params;
  }

  public String getVersion() {
    return ver[0] + "." + ver[1];
  }

  public int compareVersion(int major, int minor) {
    if (major < ver[0]) return -1;
    else if (major > ver[0]) return 1;
    else if (minor < ver[1]) return -1;
    else if (minor > ver[1]) return 1;
    else return 0;
  }

  public static String getHttpReply(int codevalue) {
    String key, ret;
    int i;

    ret = null;
    key = "" + codevalue;
    for (i=0; i<HttpReplies.length; i++) {
      if (HttpReplies[i][0].equals(key)) {
        ret = codevalue + " " + HttpReplies[i][1];
        break;
      }
    }

    return ret;
  }

  public static String getDateHeader() {
    SimpleDateFormat format;
    String ret;

    format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.US);
    format.setTimeZone(TimeZone.getTimeZone("GMT"));
    ret = "Date: " + format.format(new Date()) + " GMT";

    return ret;
  }
}

