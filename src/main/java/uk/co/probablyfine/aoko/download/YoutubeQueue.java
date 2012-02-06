package uk.co.probablyfine.aoko.download;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import uk.co.probablyfine.aoko.dao.QueueItemDao;
import uk.co.probablyfine.aoko.dao.YoutubeDao;
import uk.co.probablyfine.aoko.domain.YoutubeDownload;

public class YoutubeQueue {

	private String path;
	
	@Autowired
	YoutubeDao ytDao;
	
	@Autowired
	QueueItemDao qiDao;

	private Thread dlThread;
	
	@PostConstruct
	public void downloadVideos() {
		dlThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				YoutubeDownload yd = ytDao.next();
				try {
					int code = Runtime.getRuntime().exec(new String[] {path, "-o", "/var/tmp/"+yd.getId(), yd.getUrl()}).waitFor();
					
					if (code == 0) {
						//TODO: Verify file hash
						//TODO: if dl.hash not in QiDAO, create new item with that one.
						//TODO: Else create it and make a new queueitem with it
					} else {
						//fail, mark as failure
					}
					
					
					
					
										
				} catch (IOException e) {
					System.out.println("SOMETHING WENT HORRIBLY WRONG.");
					ytDao.dlFail(yd);
					return;
				} catch (InterruptedException e) {
					
				}
				
				
				
			}
		});
		dlThread.start();
	}
	
	public void stopDownloader() {
		
	}
	
}
