package pl.rbolanowski.tw4a.frameworks;

import java.util.List;

import org.mockito.exceptions.misusing.NullInsteadOfMockException;
import pl.rbolanowski.tw4a.test.AndroidMockitoTestCase;

import static org.mockito.Mockito.*;

public class MockitoTest extends AndroidMockitoTestCase {

    public void testVerifyHandlesNullMock() throws Exception {
        assertThrows(NullInsteadOfMockException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Exception {
                verify(null);
            }
        });
    }

    public void testVerifyMockCalls() throws Exception {
        List mockedList = mock(List.class);

        mockedList.add("hello world");
        mockedList.add(12);
        verify(mockedList).add("hello world");
        verify(mockedList, times(2)).add(any());

        mockedList.clear();
        verify(mockedList).clear();
    }

    public void testStubMethods() throws Exception {
        List mockedList = mock(List.class);
        when(mockedList.toString()).thenReturn("hello world");
        assertEquals("hello world", mockedList.toString());
    }

}

