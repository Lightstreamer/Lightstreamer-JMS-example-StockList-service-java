/*
 * Copyright (c) Lightstreamer Srl
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package jms_demo_services.config;

import java.util.Objects;

public class Configuration {

  public final String initialContextFactory;

  public final String jmsUrl;

  public final String connectionFactoryName;

  public final String topicName;

  public final String username;

  public final String password;

  private Configuration(Builder builder) {
    this.initialContextFactory = builder.initialContextFactory;
    this.jmsUrl = builder.jmsUrl;
    this.connectionFactoryName = builder.connectionFactoryName;
    this.topicName = builder.topicName;
    this.username = builder.username;
    this.password = builder.password;
  }

  public static class Builder {

    private String initialContextFactory;

    private String jmsUrl;

    private String connectionFactoryName;

    private String topicName;

    private String username;

    private String password;

    public Builder withJmsURL(String jmsUrl) {
      Objects.requireNonNull(jmsUrl, "Please provide the <jmsUrl> entry");
      this.jmsUrl = jmsUrl;
      return this;
    }

    public Builder withInitialiContextFactory(String initialCotextFactory) {
      Objects.requireNonNull(initialCotextFactory,
          "Please provide the <initialContextFactory> entry");
      this.initialContextFactory = initialCotextFactory;
      return this;
    }

    public Builder withConnectionFactoryName(String connectionFactoryName) {
      Objects.requireNonNull(connectionFactoryName,
          "Please provide the <connectionFactoryName> entry");
      this.connectionFactoryName = connectionFactoryName;
      return this;
    }

    public Builder withTopicName(String topicName) {
      Objects.requireNonNull(topicName, "Please provide the <topicName> entry");
      this.topicName = topicName;
      return this;
    }

    public Builder withCredentials(String username, String password) {
      this.username = username;
      this.password = password;
      return this;
    }

    public Configuration build() {
      return new Configuration(this);
    }

  }

}
