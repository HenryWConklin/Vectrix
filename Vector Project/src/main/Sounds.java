package main;

import java.io.BufferedInputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.newdawn.slick.openal.WaveData;
import static org.lwjgl.openal.AL10.*;

/**
 * Handles sound effects and music
 * Uses OpenAL, all methods prefixed with 'al' are from the OpenAL library
 * @author Henry
 *
 */
public class Sounds {

	//Integer IDs for different sounds
	public static final int SHOT = 0;
	public static final int KILLED = 1;
	public static final int HURT = 2;
	
	//Factor by which to scale position and velocity values
	private static final float DIST_FACTOR = 250;
	
	//Lists of sounds and active source handles
	static int[] buffers;
	static ArrayList<Integer> activeSources;
	
	/**
	 * Generates and returns a new sound source with the given parameters
	 * @param ID Sound attached to this source
	 * @param x x position of this source
	 * @param y y position of this source
	 * @param vx x velocity of this source
	 * @param vy y velocity of this source
	 * @return handle to the generated source or -1 if there was an error
	 */
	public static int genSource(int ID, double x, double y, double vx, double vy){
		int source = alGenSources();
		alSourcei(source, AL_BUFFER, buffers[ID]);
		alSource3f(source, AL_POSITION, (float)x/DIST_FACTOR, (float)y/DIST_FACTOR, 0);
		alSource3f(source, AL_VELOCITY, (float)vx/DIST_FACTOR, (float)vy/DIST_FACTOR,0);
		if (ID == SHOT) alSourcef(source, AL_GAIN, .6f);
		else alSourcef(source, AL_GAIN, 1f);
		alSourcef(source, AL_PITCH, 1f);
		
		
		if (alGetError() != AL_NO_ERROR) return -1;
		
		activeSources.add(new Integer(source));
		return source;
		
	}
	
	/**
	 * Play the sound associated with the given source handle
	 * @param source handle to a source
	 */
	public static void playSound(int source){
		if (source != -1) alSourcePlay(source);
	}
	
	/**
	 * Update the given source to the given position and velocity
	 * @param source handle to a source
	 * @param x new x position
	 * @param y new y position
	 * @param vx new x velocity
	 * @param vy new x velocity
	 */
	public static void updateSource(int source, double x, double y, double vx, double vy){
		alSource3f(source, AL_POSITION, (float)x/DIST_FACTOR, (float)y/DIST_FACTOR, 0);
		alSource3f(source, AL_VELOCITY, (float)vx/DIST_FACTOR, (float)vy/DIST_FACTOR, 0);
		
		alGetError();
	}
	
	/**
	 * Free the memory used by the given source
	 * @param source a handle to a source
	 */
	public static void deleteSource(int source){
		if (activeSources.contains(new Integer(source)));
		alDeleteSources(source);
		activeSources.remove(new Integer(source));
	}
	
	/**
	 * Sets up OpenAL, loads sounds from file
	 * @return OpenAL constant indicating success or failure
	 */
	public static int setUpSound(){
		
		try {
			AL.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		AL10.alGetError();
		
		buffers = new int[3];
		
		buffers[0] = alGenBuffers();
		
		WaveData file = WaveData.create(new BufferedInputStream(new Sounds().getClass().getResourceAsStream("/sounds/shot.wav")));
		alBufferData(buffers[0], file.format, file.data, file.samplerate);
		file.dispose();
		
		buffers[1] = alGenBuffers();
		file = WaveData.create(new BufferedInputStream(new Sounds().getClass().getResourceAsStream("/sounds/killed.wav")));
		alBufferData(buffers[1], file.format, file.data, file.samplerate);
		file.dispose();
		
		buffers[2] = alGenBuffers();
		file = WaveData.create(new BufferedInputStream(new Sounds().getClass().getResourceAsStream("/sounds/hit.wav")));
		alBufferData(buffers[2], file.format, file.data, file.samplerate);
		file.dispose();
		
		activeSources = new ArrayList<Integer>();
		if (AL10.alGetError() != AL10.AL_NO_ERROR) return AL10.AL_FALSE;
		
		return AL10.AL_TRUE;
		
	}
	
	/**
	 * Indicates whether the given source is currently plaing a sound
	 * @param source
	 * @return Is the sound currently playing
	 */
	public static boolean isPlaying(int source){
		int isPlaying = alGetSourcei(source, AL_SOURCE_STATE);
		return isPlaying == AL_PLAYING;
	}
	
	/**
	 * Updates the listener position and velocity
	 * @param x new x position
	 * @param y new y position
	 * @param vx new x velocity
	 * @param vy new y velocity
	 */
	public static void updateListener(double x, double y, double vx, double vy){
		alListener3f(AL_POSITION, (float)x/DIST_FACTOR, (float)y/DIST_FACTOR, 0);
		alListener3f(AL_VELOCITY, (float)vx/DIST_FACTOR, (float)vy/DIST_FACTOR, 0);
		FloatBuffer ori = BufferUtils.createFloatBuffer(6).put(new float[] { 0.0f, 0.0f, -1.0f,  0.0f, 1.0f, 0.0f });
		ori.flip();
		alListener(AL_ORIENTATION, ori);
		alGetError();
	}
	
	/**
	 * Clear memory associated with loaded sounds and sources
	 */
	public static void decon(){
		for (int i = 0; i < buffers.length; i++) alDeleteBuffers(buffers[i]);
		for (int i = 0; i < activeSources.size(); i++) alDeleteSources(activeSources.get(i));
	}
}
