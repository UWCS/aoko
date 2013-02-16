package uk.co.probablyfine.aoko.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;

import uk.co.probablyfine.aoko.dao.AccountDao;
import uk.co.probablyfine.aoko.dao.MusicFileDao;
import uk.co.probablyfine.aoko.domain.Account;
import uk.co.probablyfine.aoko.domain.MusicFile;
import uk.co.probablyfine.aoko.download.ArtDownloader;

@RunWith(MockitoJUnitRunner.class)
public class FileUploadHandlerTest {

	@InjectMocks private FileUploadHandler handler;
	@Mock private ArtDownloader mockArts;
	@Mock private MusicFileDao mockMusic;
	@Mock private QueueService mockQueueService;
	@Mock private AccountDao mockAccounts;
	@Mock private MultipartFile mockMultipartFile;
	@Mock private FileUtils mockUtils;
	@Mock private FileMetadataTagger mockTagger;
	@Mock private MusicFile mockMusicfile;
	
	@Test public void shouldQueueOriginalFile_whenFileExistsInDatabase() throws IOException, NoSuchAlgorithmException {
		File mockFile = mock(File.class);
		String hexVal = "foobar";

		when(mockUtils.downloadToTemporaryFile(mockMultipartFile)).thenReturn(mockFile);
		when(mockUtils.getHashFromFile(mockFile)).thenReturn(hexVal);
		when(mockMusic.containsFile(hexVal)).thenReturn(true);
		when(mockMusic.getFromUniqueId(hexVal)).thenReturn(mockMusicfile);
		
		handler.processFile(mockMultipartFile, "testuser");
		
		verify(mockQueueService).queueTrack(any(Account.class),any(MusicFile.class));
		
	}
	
	@Test public void shouldReadMetaData_whenFileDoesNotExistInDatabase() throws IOException, NoSuchAlgorithmException {
		File mockFile = mock(File.class);
		String hexVal = "foobar";

		when(mockMultipartFile.getOriginalFilename()).thenReturn("foo.bar");
		when(mockUtils.downloadToTemporaryFile(mockMultipartFile)).thenReturn(mockFile);
		when(mockUtils.getHashFromFile(mockFile)).thenReturn(hexVal);
		when(mockMusic.containsFile(hexVal)).thenReturn(false);
		
		handler.processFile(mockMultipartFile, "testuser");
		
		verify(mockQueueService).queueTrack(any(Account.class),any(MusicFile.class));
		
	}

}
