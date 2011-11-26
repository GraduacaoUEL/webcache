/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Hash;

/**
 *
 * @author Vinicius
 */
public class ArquivoRemoto {
    private String url;
    private long ping;
    private String ip;
    private long vida;

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the ping
     */
    public long getPing() {
        return ping;
    }

    /**
     * @param ping the ping to set
     */
    public void setPing(long ping) {
        this.ping = ping;
    }

    /**
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * @return the vida
     */
    public long getVida() {
        return vida;
    }

    /**
     * @param vida the vida to set
     */
    public void setVida(long vida) {
        this.vida = vida;
    }
}
