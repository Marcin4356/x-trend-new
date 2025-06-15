package com.marcin01.xtrend.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.kohsuke.github.GHRepositorySearchBuilder;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@RestController
public class RepositoryDetailsController {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryDetailsController.class);

    @Value("${GITHUB_USERNAME}")
    private String githubUsername;

    @Value("${GITHUB_PASSWORD}")
    private String githubPassword;

    @Value("${CONSUMER_KEY}")
    private String consumerKey;

    @Value("${CONSUMER_SECRET}")
    private String consumerSecret;

    @Value("${ACCESS_TOKEN}")
    private String accessToken;

    @Value("${ACCESS_TOKEN_SECRET}")
    private String accessTokenSecret;

    @RequestMapping("/")
    public String getRepos() throws IOException {
        GitHub github = new GitHubBuilder()
                .withPassword(githubUsername, githubPassword)
                .build();

        GHRepositorySearchBuilder builder = github.searchRepositories();

        return "Welcome to x-trend!";
    }

    @GetMapping("/trends")
    public Map<String, String> getTwitterTrends(@RequestParam("placeid") String trendPlace,
                                                @RequestParam("count") String trendCount) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
          .setOAuthConsumerKey(consumerKey)
          .setOAuthConsumerSecret(consumerSecret)
          .setOAuthAccessToken(accessToken)
          .setOAuthAccessTokenSecret(accessTokenSecret);

        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        Map<String, String> trendDetails = new HashMap<>();

        try {
            Trends trends = twitter.getPlaceTrends(Integer.parseInt(trendPlace));
            int count = 0;
            for (Trend trend : trends.getTrends()) {
                if (count < Integer.parseInt(trendCount)) {
                    trendDetails.put(trend.getName(), trend.getURL());
                    count++;
                }
            }
        } catch (TwitterException e) {
            logger.error("Twitter API error: {}", e.getMessage());
            trendDetails.put("error", "Twitter API error");
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            trendDetails.put("error", "Unexpected error");
        }

        return trendDetails;
    }
}
