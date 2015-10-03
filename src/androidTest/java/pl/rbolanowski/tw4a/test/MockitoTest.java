package pl.rbolanowski.tw4a.test;

import java.util.List;

import org.mockito.exceptions.misusing.NullInsteadOfMockException;

import static org.mockito.Mockito.*;

public class MockitoTest extends AndroidMockitoTestCase {

    public void testVerifyHandlesNullMock() throws Exception {
        boolean catched = false;
        try {
            verify(null);
        }
        catch (NullInsteadOfMockException e) { catched = true; }
        assertTrue(catched);
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

