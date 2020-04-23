package jms_demo_services.config;

import java.util.Objects;

public class Configuration {

  public final String initialContextFactory;

  public final String jmsUrl;

  public final String connectionFactoryName;

  public final String topic;

  public final String username;

  public final String password;

  private Configuration(String initialContextFactory, String jmsUrl,
      String connectionFactoryName, String topic, String username, String password) {

    this.initialContextFactory = initialContextFactory;
    this.jmsUrl = jmsUrl;
    this.connectionFactoryName = connectionFactoryName;
    this.topic = topic;
    this.username = username;
    this.password = password;
  }

  public static class Builder {

    private String initialContextFactory;

    private String jmsUrl;

    private String connectionFactoryName;

    private String topic;

    private String username;

    private String password;

    public Builder withJmsURL(String jmsUrl) {
      Objects.requireNonNull(jmsUrl, "Please provide the <jmsUrl> entry");
      this.jmsUrl = jmsUrl;
      return this;
    }

    public Builder withInitialiContextFactory(String initialCotextFactory) {
      Objects.requireNonNull(initialCotextFactory, "Please provide the <initialContextFactory> entry");
      this.initialContextFactory = initialCotextFactory;
      return this;
    }

    public Builder withConnectionFactoryName(String connectionFactoryName) {
      Objects.requireNonNull(connectionFactoryName, "Please provide the <connectionFactoryName> entry");
      this.connectionFactoryName = connectionFactoryName;
      return this;
    }

    public Builder withTopic(String topic) {
      Objects.requireNonNull(topic,  "Please provide the <topic> entry");
      this.topic = topic;
      return this;
    }

    public Builder withCredentials(String username, String password) {
      this.username = username;
      this.password = password;
      return this;
    }

    public Configuration build() {
      return new Configuration(initialContextFactory, jmsUrl, connectionFactoryName, topic,
          username, password);
    }

  }

}
