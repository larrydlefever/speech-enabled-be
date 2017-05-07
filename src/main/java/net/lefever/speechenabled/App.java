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
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

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
		System.out.println("Speech-Enabled VR - Back-end");

		staticFiles.location("/public");

		post("/audio", (req, res) -> handleAudio(req, res));
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
			
			File outFileAlexa = new File("/Users/user/javaProjects/speechenabled/voiceInput-alexa.wav");
			changeBitrate(outFile, outFileAlexa);

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
	
	/**
	 * For Alexa: 16k sampling-rate, 16-bit resolution, mono, signed, little-endian, WAV-file
	 * see: https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/speechrecognizer
	 * 	
	 *	NOTE: use "mdls" at cmdln, to check the attributes of a wav-file
	 *		  Audacity can be misleading: you load a 16-bit audio-file, but it indicates as 32-bit
	 * 
	 * @param source
	 * @param output
	 * @throws Exception
	 */
	private static void changeBitrate(File source,File output) throws Exception 
	{
		  AudioFormat format=new AudioFormat(16000,16,1,true,false);
		  AudioInputStream in=AudioSystem.getAudioInputStream(source);
		  AudioInputStream convert=AudioSystem.getAudioInputStream(format,in);
		  AudioSystem.write(convert,AudioFileFormat.Type.WAVE,output);
	}
}
