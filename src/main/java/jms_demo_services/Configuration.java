package jms_demo_services;

import java.util.Objects;

public class Configuration {

  public final String initialContextFactory;

  public final String providerURL;

  public final String connectionFactoryName;

  public final String topic;

  public final String username;

  public final String password;

  private Configuration(String initialContextFactory, String providerURL,
      String connectionFactoryName, String topic, String username, String password) {

    this.initialContextFactory = initialContextFactory;
    this.providerURL = providerURL;
    this.connectionFactoryName = connectionFactoryName;
    this.topic = topic;
    this.username = username;
    this.password = password;
  }

  static class Builder {

    private String initialContextFactory;

    private String providerURL;

    private String connectionFactoryName;

    private String topic;

    private String username;

    private String password;

    Builder withProviderURL(String providerURL) {
      Objects.requireNonNull(providerURL);
      this.providerURL = providerURL;
      return this;
    }

    Builder withInitialiContextFactory(String initialCotextFactory) {
      Objects.requireNonNull(initialCotextFactory);
      this.initialContextFactory = initialCotextFactory;
      return this;
    }

    Builder withConnectionFactoryName(String connectionFactoryName) {
      Objects.requireNonNull(connectionFactoryName);
      this.connectionFactoryName = connectionFactoryName;
      return this;
    }

    Builder withTopic(String topic) {
      Objects.requireNonNull(topic);
      this.topic = topic;
      return this;
    }

    Builder withCredentials(String username, String password) {
      this.username = username;
      this.password = password;
      return this;
    }

    Configuration build() {
      return new Configuration(initialContextFactory, providerURL, connectionFactoryName, topic,
          username, password);
    }

  }

}
