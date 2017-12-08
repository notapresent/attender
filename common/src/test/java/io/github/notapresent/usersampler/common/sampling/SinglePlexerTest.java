package io.github.notapresent.usersampler.common.sampling;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SinglePlexerTest {
    A a;

    @Before
    public void setUp() {
        a = new A();
    }

    @Test
    public void multiSend() {
        assertEquals("Something", a.val);
    }
}

class A {
    public String val = "Something";
}