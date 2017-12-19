package io.github.notapresent.usersampler.gaeapp.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import io.github.notapresent.usersampler.common.sampling.BaseStatus;
import io.github.notapresent.usersampler.common.sampling.UserStatus;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static io.github.notapresent.usersampler.gaeapp.storage.SamplePayloadCompactor.deflate;
import static io.github.notapresent.usersampler.gaeapp.storage.SamplePayloadCompactor.inflate;
import static org.junit.Assert.*;

public class SamplePayloadCompactorTest {

    private Map<String, UserStatus> original = new ImmutableMap.Builder<String, UserStatus>()
            .put("u1", BaseStatus.ONLINE)
            .put("u2", BaseStatus.ONLINE)
            .put("u3", BaseStatus.PRIVATE)
            .build();

    private Map<UserStatus, List<String>> deflated = new ImmutableMap.Builder<UserStatus, List<String>>()
            .put(BaseStatus.ONLINE, Lists.newArrayList("u1", "u2"))
            .put(BaseStatus.PRIVATE, Lists.newArrayList("u3"))
            .build();

    @Test
    public void deflateShouldGroupByStatus() {
        Map<UserStatus, List<String>> result = deflate(original);
        assertEquals(2, result.keySet().size());
        assertTrue(result.get(BaseStatus.ONLINE).contains("u1"));
        assertTrue(result.get(BaseStatus.ONLINE).contains("u2"));
        assertTrue(result.get(BaseStatus.PRIVATE).contains("u3"));
    }

    @Test
    public void inflateShouldExpandGroups() {
        Map<String, UserStatus> result = inflate(deflated);
        assertEquals(3, result.size());
        assertEquals(BaseStatus.ONLINE, result.get("u1"));
        assertEquals(BaseStatus.ONLINE, result.get("u2"));
        assertEquals(BaseStatus.PRIVATE, result.get("u3"));
    }

}