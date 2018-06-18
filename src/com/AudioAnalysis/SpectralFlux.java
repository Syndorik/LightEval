package com.AudioAnalysis;

import java.awt.Color;
import java.io.ByteArrayInputStream;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import com.visualization.*;
import com.io.*;
import com.musicg.wave.extension.*;
public class SpectralFlux {
	public static final int THRESHOLD_WINDOW_SIZE = 25;
	public static final double MULTIPLIER = 2f;

	public static void main(String[] args) throws IOException {
		
		List <Double> spectralFlux = new ArrayList<Double>();
		List <Double> threshold = new ArrayList<Double>();
		List <Double> prunned = new ArrayList<Double>();
		List <Double> peaks = new ArrayList<Double>();
		List <Double> onAndOff = new ArrayList<Double>();
		
		String address = "C:\\Users\\alexa\\eclipse-workspace\\BeatDetection\\src\\com\\TestSound\\cddc.wav";
		WavfileDecod myWav = new WavfileDecod(address);
		System.out.println(myWav.getSampleRate());
		
		double[][] spectrogram = myWav.getWav().getSpectrogram(2048,0).getAbsoluteSpectrogramData();
		
		
		
		flux(spectralFlux, spectrogram);
		threshold(threshold,spectralFlux);
		compare(threshold,spectralFlux,prunned);
		peaklist(peaks, prunned);
		beats(peaks,onAndOff);
		System.out.println("siseof spectralflux"+ spectralFlux.size());
		System.out.println("siseof peaks"+ onAndOff.size());
		System.out.println("siseof peaks"+ spectrogram.length);
		
		
		Plot plot = new Plot("Spectral Flux", 1024,512);
		Plot plot2 = new Plot("Spectral Flux", 1024,512);
		//plot.plot(onAndOff, 1, Color.red);
		//plot.plot(prunned, 1, Color.red);
		
		List <Float> sF = new ArrayList<Float>();
		List <Float> th = new ArrayList<Float>();
		
		for(int k=0; k< spectralFlux.size(); k++) {
			double d = spectralFlux.get(k);
			float f = (float) d;
			double de = threshold.get(k);
			float fe = (float) de;
			sF.add(f);
			th.add(fe);
		}
		
		
		plot.plot(sF, 1, Color.red,true);
		plot.plot( th, 1, Color.green,true ) ;
		plot2.plot(onAndOff, 1, Color.red);
		
		
		
	}
	
	public static void beats(List<Double> peaks, List<Double> onAndOff) {
		onAndOff.add((double) 0);
		for(int k=0; k<peaks.size()-1; k++) {
			if(peaks.get(k) >0) {
				onAndOff.add((double) 1);
			}
			else {
				onAndOff.add((double) 0);
			}
		}
		
	}
	
	public static void peaklist(List<Double> peaks, List<Double> prunned) {
		peaks.add((double)0);
		for(int l =0; l<prunned.size()-1; l++) {
			if(prunned.get(l)>prunned.get(l+1)) {
				peaks.add(prunned.get(l));
			}
			else {
				peaks.add((double) 0);
			}
		}
	}
	
	public static void compare(List<Double> threshold, List<Double> spectralFlux, List<Double> prunned) {
		boolean bool = true;
		double previous =0;
		
		for( int i = 0; i < threshold.size(); i++ )
		{
		   if( (threshold.get(i) <= spectralFlux.get(i)) ) {
			  if(bool) {
				  prunned.add(previous);
			  }
			  else {
			      prunned.add( spectralFlux.get(i) - threshold.get(i) );
			      previous = spectralFlux.get(i) - threshold.get(i);
			      bool = true;
			  }
		   }
		   
		   else {
		      prunned.add( (double)0 );
		      bool = false;
		   }
		}
		
	}
	
	public static void threshold(List<Double> threshold, List<Double> spectralFlux) {
		for( int i = 0; i < spectralFlux.size(); i++ )
	      {
	         int start = Math.max( 0, i - THRESHOLD_WINDOW_SIZE );
	         int end = Math.min( spectralFlux.size() - 1, i + THRESHOLD_WINDOW_SIZE );
	         double mean = 0;
	         for( int j = start; j <= end; j++ )
	            mean += spectralFlux.get(j);
	         mean /= (end - start+1);
	         threshold.add( mean * MULTIPLIER );
	      }
	}
	
	public static void flux(List<Double> spectralFlux, double[][] spectrogram) {
		int len = spectrogram[0].length;
		int dlen = spectrogram.length;
		double flux =0;
		double value = 0;
		for(int k =0; k <dlen; k+=1) { 
			flux = 0;
			value =0;
			for(int i =0; i<len; i++) {
				if(k ==0) {
					value = spectrogram[k][i];
					flux += value < 0? 0: value;
				}
				else {
					value= spectrogram[k][i] - spectrogram[k-1][i];
					flux += value < 0? 0: value;
				}
				
			}
			spectralFlux.add((double) flux);
		}
		
	}

	/*
	public static void generateWav(String Oadd, WavfileDecod myWav) throws IOException {
		AudioInputStream stream = new AudioInputStream(
				new ByteArrayInputStream(Short2Byte(myWav.getAmp())),
				new AudioFormat((int) myWav.getSampleRate(),(int) myWav.getbitPerSample(), myWav.getNChannels(),true,false),
				myWav.getData().length/4);
		
		File newFile = new File("C:\\Users\\alexa\\eclipse-workspace\\BeatDetection\\src\\com\\TestSound\\psdp.wav");
		AudioSystem.write(stream,Type.WAVE, newFile);
		
		Spectrogram spectrogram = new Spectrogram(myWav.getWav());
		GraphicRender render = new GraphicRender();
		render.renderSpectrogram(spectrogram, "C:\\Users\\alexa\\eclipse-workspace\\BeatDetection\\src\\com\\TestSound\\cc.jpg");
	}
	*/
	
	
	public static byte [] Short2Byte(short [] input)
	{
		  int short_index, byte_index;
		  int iterations = input.length;

		  byte [] buffer = new byte[input.length * 2];

		  short_index = byte_index = 0;

		  for(/*NOP*/; short_index != iterations; /*NOP*/)
		  {
		    buffer[byte_index]     = (byte) (input[short_index] & 0x00FF); 
		    buffer[byte_index + 1] = (byte) ((input[short_index] & 0xFF00) >> 8);

		    ++short_index; byte_index += 2;
		  }

		  return buffer;
		}
}