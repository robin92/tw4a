package pl.rbolanowski.tw4a.frameworks;

import android.support.test.runner.AndroidJUnit4;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.exceptions.misusing.NullInsteadOfMockException;

import pl.rbolanowski.tw4a.test.AndroidMockitoTestCase;

import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class MockitoTest extends AndroidMockitoTestCase {

    @Test(expected = NullInsteadOfMockException.class)
    public void verifyHandlesNullMock() throws Exception {
        verify(null);
    }

    @Test public void verifyMockCalls() throws Exception {
        List mockedList = mock(List.class);

        mockedList.add("hello world");
        mockedList.add(12);
        verify(mockedList).add("hello world");
        verify(mockedList, times(2)).add(any());

        mockedList.clear();
        verify(mockedList).clear();
    }

    @Test public void stubMethods() throws Exception {
        List mockedList = mock(List.class);
        when(mockedList.toString()).thenReturn("hello world");
        assertEquals("hello world", mockedList.toString());
    }

}

