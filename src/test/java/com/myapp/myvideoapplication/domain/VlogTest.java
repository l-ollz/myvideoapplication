package com.myapp.myvideoapplication.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.myapp.myvideoapplication.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VlogTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Vlog.class);
        Vlog vlog1 = new Vlog();
        vlog1.setId(1L);
        Vlog vlog2 = new Vlog();
        vlog2.setId(vlog1.getId());
        assertThat(vlog1).isEqualTo(vlog2);
        vlog2.setId(2L);
        assertThat(vlog1).isNotEqualTo(vlog2);
        vlog1.setId(null);
        assertThat(vlog1).isNotEqualTo(vlog2);
    }
}
