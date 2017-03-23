package com.homescreenarcade.mazeman;

import java.util.HashMap;


import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.homescreenarcade.R;


// Sound Manager, provide method to play sounds and music
public class SoundEngine {
	private static final int  EATFOOD = 1,  EATGHOST = 2, DIE = 3, READY = 4,
			GAMEOVER = 5;
	//private static final int EATCHERRY = 6;
	
	private SoundPool sounds;
	private HashMap<Integer, Integer> soundsMap;
//	private MediaPlayer music;
	private Context context;
	private AudioManager mgr;

	public SoundEngine(Context context) {
		this.context = context;

		sounds = new SoundPool(5, AudioManager.STREAM_RING, 0);

		soundsMap = new HashMap<Integer, Integer>();
		soundsMap.put(EATFOOD, sounds.load(context, R.raw.pacmon_waka_waka, 1));
		soundsMap.put(EATGHOST, sounds.load(context, R.raw.pacmon_eating_ghost, 1));
		soundsMap.put(DIE, sounds.load(context, R.raw.pacmon_dies, 1));
		soundsMap.put(READY, sounds.load(context, R.raw.pacmon_opening_song,1));
		soundsMap.put(GAMEOVER, sounds.load(context, R.raw.pacmon_opening_song, 1));
		//soundsMap.put(EATCHERRY, sounds.load(context, R.raw.pacmon_eating_cherry, 1));
		
		AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		
	    // the music that is played at the beginning
//	    music = MediaPlayer.create(context, R.raw.gameplaymusic);
//	    music.setLooping(true);
	}
	
	//play the sound depends on the input request
	public void play(int sound) {
		AudioManager mgr = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		float streamVolumeCurrent = mgr
				.getStreamVolume(AudioManager.STREAM_RING);
		float streamVolumeMax = mgr
				.getStreamMaxVolume(AudioManager.STREAM_RING);
		float volume = streamVolumeCurrent / streamVolumeMax;

		sounds.play(soundsMap.get(sound), volume, volume, 1, 0, 1.f);
	}
	
	//when pacmon eats food
	public void playEatFood(){
		play(EATFOOD);
	}
	
	//when pacmon eat ghost in power mode
	public void playEatGhost(){
		play(EATGHOST);
	}
	
	public void playEatCherry(){
		//play(EATCHERRY);
	}
	
	//when pacmon die
	public void playDie(){
		play(DIE);
	}
	
	public void playMusic(){
//		music.start();
	}
	
	public void stopMusic(){
//		music.pause();
	}
	
	public void endMusic(){
		sounds.release();
		
//		music.release();
	}
	
	public void playReady(){
		play(READY);
	}
	
	public void playWin(){
		
	}
	
	public void playGameOver(){
		play(GAMEOVER);
	}
	
}
