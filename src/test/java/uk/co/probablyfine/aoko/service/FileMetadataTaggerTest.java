package uk.co.probablyfine.aoko.service;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FileMetadataTaggerTest {

	@InjectMocks private FileMetadataTagger tagger = new FileMetadataTagger();
	@Mock private FileUtils mockUtils;
	@Mock private File mockFile;
	@Mock private AudioFile mockAudioFile;
	@Mock private Tag mockTag;
	
	@Test public void shouldReturnEmptyMetadata_whenPassedInvalidFile() throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
		when(mockUtils.getAudioFile(mockFile)).thenThrow(new IOException());
		Map<String, String> metaData = tagger.getMetaData(mockFile);
		assertEquals(0,metaData.size());
	}
	
	@Test public void shouldReturnEmptyMetadata_whenFileHasNoTags() throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
		when(mockUtils.getAudioFile(mockFile)).thenReturn(mockAudioFile);
		when(mockAudioFile.getTag()).thenReturn(mockTag);
		when(mockTag.getFirst(any(FieldKey.class))).thenThrow(new KeyNotFoundException());
		
		Map<String, String> metaData = tagger.getMetaData(mockFile);
		assertEquals(0,metaData.size());
	}
	
	@Test public void shouldReturnMetadata_whenFileHasTags() throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
		when(mockUtils.getAudioFile(mockFile)).thenReturn(mockAudioFile);
		when(mockAudioFile.getTag()).thenReturn(mockTag);
		when(mockTag.getFirst(FieldKey.ARTIST)).thenReturn("foovalue");
		
		Map<String, String> metaData = tagger.getMetaData(mockFile);
		assertEquals(1,metaData.size());
	}

}
