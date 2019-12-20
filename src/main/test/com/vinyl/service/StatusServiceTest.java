package com.vinyl.service;

import com.vinyl.model.Status;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
public class StatusServiceTest {
    @MockBean
    private StatusService mockStatusService;

    @Test
    public void findStatusByIdTest(){
        final Status status = new Status(1L,"active");

        Mockito.when(mockStatusService.findById(1L)).thenReturn(status);

        Status testStatus = mockStatusService.findById(1L);

        Assert.assertEquals(status, testStatus);
        verify(mockStatusService).findById(1L);
    }
}
