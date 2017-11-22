// Copyright (C) 2018 Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License"),
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.its.jira;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class EncryptTest {

  @Test
  public void testDecrypt() throws Exception {
    String plainText = "password";
    String encrypted = (Encrypt.encrypt(plainText));
    assertThat(plainText).isEqualTo(Encrypt.decrypt(encrypted));
  }

  @Test
  public void testEncodeBase64() throws Exception {
    String toEncode = "username:password";
    String expected = "dXNlcm5hbWU6cGFzc3dvcmQ=";
    assertThat(expected).isEqualTo(Encrypt.encodeBase64(toEncode));
  }

  @Test
  public void testEncrypt() throws Exception {
    String plainText = "password";
    String encrypted = (Encrypt.encrypt(plainText));
    assertThat(Encrypt.decrypt(encrypted)).isEqualTo(plainText);
  }
}
