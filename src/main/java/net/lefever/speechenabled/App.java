package net.lefever.speechenabled;

import static spark.Spark.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;

import spark.Request;
import spark.Response;

/**
 * Hello world!
 *
 */
public class App 
{
	public static void main(String[] args)
	{
		System.out.println( "Hello World!" );

		staticFiles.location("/public");

		post("/audio", (req, res) -> handleAudio(req, res));

		//get("/audio", (req, res) -> handleAudio(req, res));
	}

	private static String handleAudio(Request req, Response resp)
	{
		System.out.println("handleAudio called");

		try
		{
			MultipartConfigElement multipartConfigElement = new MultipartConfigElement("/tmp");
			req.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);

			InputStream in = req.raw().getPart("voiceInputUpload").getInputStream();			
			File outFile = new File("/Users/user/javaProjects/speechenabled/voiceInput.wav");
			Files.copy(in, outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

			System.out.println("uploaded file stored");
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{

		}

		return "audio-uploaded";
	}
}
