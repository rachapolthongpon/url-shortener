package com.rthongpon.url_shortener.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class Base62Test {
    @Test
    void encodeBase62_convertsNumberCorrectly() {
        // 125 = 2*62 + 1 → "21"
        assertThat(Base62.encodeBase62(125)).isEqualTo("21");
    }

    @Test
    void randomCode_isAlways7CharactersOfBase62() {
        for (int i = 0; i < 1000; i++) {
            String code = Base62.randomCode();
            assertThat(code).hasSize(7).matches("[0-9a-zA-Z]+");
        }
    }
}
