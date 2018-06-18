package com.io;
import com.musicg.wave.*;

public class WavfileDecod {
	private String address;
	private int nchannel;
	private int  bitPerSample;
	private long sampleRate;
	private Wave wavFile;
	private byte[] data;
	private int len;
	private short[] amplitude;
	
	public WavfileDecod(String address) {
		this.address = address;
		this.wavFile =  new Wave(address);
		WaveHeader waveHeader = wavFile.getWaveHeader();
		this.nchannel = waveHeader.getChannels();
		this.bitPerSample = waveHeader.getBitsPerSample();
		this.sampleRate = waveHeader.getSampleRate();
		this.data = wavFile.getBytes();
		this.len = (int) wavFile.length();
		this.amplitude = wavFile.getSampleAmplitudes();
	}
	
	public Wave getWav() {
		return this.wavFile;
	}
	
	public short[] getAmp() {
		return this.amplitude;
	}
	
	public byte[] getData() {
		return this.data;
	}
	
	public long getSampleRate() {
		return this.sampleRate;
	}
	
	public long getbitPerSample() {
		return this.bitPerSample;
	}
	
	public String getAddress() {
		return this.address;
	}
	
	public int getNChannels() {
		return this.nchannel;
	}
	
	

}
