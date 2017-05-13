package net.lefever.speechenabled;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import net.lefever.speechenabled.dto.Message;

public class RESTClient
{
    public static enum EndPoint
    {
        TEST("/test"),
        TEST_GET("/testGet");

        String url;
 
        EndPoint(String url)
        {
            this.url = url;
        }
    }

    private String baseUrl;

    public RESTClient(String url)
    {
        this.baseUrl = url;
    }

    public static void main(String[] args)
    {
        RESTClient client = new RESTClient("http://localhost:8080");

        Message inMsg = new Message();
        inMsg.setStatus("foo");

        Message message = client.doPost(EndPoint.TEST, inMsg, Message.class);
        System.out.println("status:" + message.getStatus());
        
        message = client.doGet(EndPoint.TEST_GET, Message.class);
        System.out.println("status:" + message.getStatus());
    }

    public <T> T doPost(EndPoint endPoint, Message inMsg, Class<T> outMsgtype)
    {
        HttpURLConnection conn = null;
        JsonWriter writer = null;
        JsonReader reader = null;

        try
        {
            URL url = new URL(getBaseUrl() + endPoint.url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            Gson gson = new Gson();
            OutputStream out = conn.getOutputStream();
            writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
            gson.toJson(inMsg, inMsg.getClass(), writer);

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                throw new RuntimeException("Failed: HTTP error code: " + conn.getResponseCode());
            }

            reader = new JsonReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            T message = gson.fromJson(reader, outMsgtype);

            return message;
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        } 
        finally
        {
            if (null != conn)
            {
                conn.disconnect();
            }
            if (null != reader)
            {
                try
                {
                    reader.close();
                } 
                catch (IOException e)
                {
                }
            }
            if (null != writer)
            {
                try
                {
                    writer.close();
                } 
                catch (IOException e)
                {
                }
            }
        }

        return null;
    }

    public <T> T doGet(EndPoint endPoint, Class<T> outMsgtype)
    {
        HttpURLConnection conn = null;
        JsonReader reader = null;

        try
        {

            URL url = new URL(getBaseUrl() + endPoint.url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200)
            {
                throw new RuntimeException("Failed: HTTP error code: " + conn.getResponseCode());
            }

            Gson gson = new Gson();
            reader = new JsonReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            T message = gson.fromJson(reader, outMsgtype);

            return message;
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        } 
        finally
        {
            if (null != conn)
            {
                conn.disconnect();
            }
        }

        return null;
    }

    protected String getBaseUrl()
    {
        return this.baseUrl;
    }
}
