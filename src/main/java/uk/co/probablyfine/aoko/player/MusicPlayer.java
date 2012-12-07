package uk.co.probablyfine.aoko.player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import uk.co.probablyfine.aoko.dao.QueueItemDao;
import uk.co.probablyfine.aoko.domain.QueueItem;

@Service
public class MusicPlayer {

	private final Logger log = LoggerFactory.getLogger(MusicPlayer.class);
	
	@Value("${player.path}") private String playerPath;
	@Value("${player.timeout}") private long playerTimeout;
	@Value("${media.repository}") private String downloadPath;
	@Autowired private QueueItemDao qiDao;

	private Process playTrackProcess;
	private ExecutorService executor;
	
	@PostConstruct
	public void play() throws InterruptedException {
		
		this.executor = Executors.newSingleThreadExecutor();
		
		final Timer playerTimer = new Timer();
		
		final TimerTask playerTask = new TimerTask() {
			public void run() {
				final QueueItem qi = qiDao.nextTrack();
				if (qi != null) playTrack(qi);
			}
		};
		
		playerTimer.schedule(playerTask, 0, 2000);
		
	}    

	public void playTrack(final QueueItem qi) {
		
		log.debug("Trying to play track - {} - {}",qi.getFile().getLocation(), qi.getFile().getMetaData().get("originalname"));
		
		try {
		
			qiDao.startedPlaying(qi);

			//Submit player job to the executor pool with a timeout
			int code = executor.submit(new Callable<Integer>() {

				@Override
				public Integer call() throws Exception {
					
					playTrackProcess = Runtime.getRuntime().exec(new String[] {playerPath, downloadPath+qi.getFile().getLocation()});
				
					final BufferedReader reader = new BufferedReader(new InputStreamReader(playTrackProcess.getInputStream()));
					
					String line;
					
					while ((line = reader.readLine()) != null) {
						log.trace(line);
					}
					
					return playTrackProcess.waitFor();
					
				}
				
			}).get(playerTimeout, TimeUnit.SECONDS);
			
			log.debug("Player exited with code = {}",code);
			
		} catch (InterruptedException e) {
			log.error("InterruptedException: ",e);
		} catch (ExecutionException e) {
			log.error("Error executing player for {}, aborting.",qi,e);
		} catch (TimeoutException e) {
			//This is debug, as timing out on playing a track is not an error.
			log.debug("Player timed out on {}",qi);
			stopTrack();
		} finally {
			qiDao.finishedPlaying(qi);
		}
	}
	
	public void stopTrack() {
		log.debug("Stopping track now.");
		this.playTrackProcess.destroy();
	}
	
}