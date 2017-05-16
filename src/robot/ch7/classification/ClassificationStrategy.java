package robot.ch7.classification;

import robot.ch7.core.NewsStory;
import robot.ch7.core.NewsStoryGroup;

public interface ClassificationStrategy {
    public void assignTopicToCluster(NewsStoryGroup cluster);
    public void assignTopicToStory(NewsStory newsStory);
}
