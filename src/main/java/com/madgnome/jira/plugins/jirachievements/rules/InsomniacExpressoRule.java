package com.madgnome.jira.plugins.jirachievements.rules;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.changehistory.ChangeHistoryItem;
import com.atlassian.jira.issue.changehistory.ChangeHistoryManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.madgnome.jira.plugins.jirachievements.data.ao.Achievement;
import com.madgnome.jira.plugins.jirachievements.data.ao.AchievementRefEnum;
import com.madgnome.jira.plugins.jirachievements.data.ao.UserWrapper;
import com.madgnome.jira.plugins.jirachievements.data.services.IAchievementDaoService;
import com.madgnome.jira.plugins.jirachievements.data.services.IUserAchievementDaoService;
import com.madgnome.jira.plugins.jirachievements.data.services.IUserWrapperDaoService;
import com.madgnome.jira.plugins.jirachievements.utils.data.IssueSearcher;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsomniacExpressoRule extends AbstractRule implements IRule
{
  private final IssueSearcher issueSearcher;
  private final ChangeHistoryManager changeHistoryManager;

  private final Map<AchievementRefEnum, TimeRange> timeRangeForAchievement;

  public InsomniacExpressoRule(JiraAuthenticationContext jiraAuthenticationContext, IUserWrapperDaoService userWrapperDaoService, IAchievementDaoService achievementDaoService, IUserAchievementDaoService userAchievementDaoService, IssueSearcher issueSearcher, ChangeHistoryManager changeHistoryManager)
  {
    super(jiraAuthenticationContext, userWrapperDaoService, achievementDaoService, userAchievementDaoService);
    this.issueSearcher = issueSearcher;
    this.changeHistoryManager = changeHistoryManager;

    timeRangeForAchievement = getTimeRangeForAchievement();
  }

  @Override
  public AchievementRefEnum getAchievementRef()
  {
    return null;
  }

  @Override
  public void check()
  {
    List<Issue> issues = issueSearcher.searchIssues("status WAS IN (Open, Resolved, Closed)");

    for (Issue issue : issues) 
    {
      checkTime(issue.getCreated(), issue.getReporterUser().getName(), AchievementRefEnum.INSOMNIAC_USER);
      checkTime(issue.getCreated(), issue.getReporterUser().getName(), AchievementRefEnum.EXPRESSO_USER);
      checkTime(issue.getResolutionDate(), getResolverName(issue), AchievementRefEnum.INSOMNIAC_DEVELOPER);
      checkTime(issue.getResolutionDate(), getResolverName(issue), AchievementRefEnum.EXPRESSO_DEVELOPER);
    }
  }

  private void checkTime(Timestamp timestamp, String username, AchievementRefEnum achievementRefEnum)
  {
    if (timestamp == null)
    {
      return;
    }

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(timestamp);

    int resolutionHour = calendar.get(Calendar.HOUR_OF_DAY);
    TimeRange timeRange = timeRangeForAchievement.get(achievementRefEnum);
    if (timeRange.isBetween(resolutionHour))
    {
      UserWrapper userWrapper = userWrapperDaoService.get(username);
      if (userWrapper != null)
      {
        Achievement achievement = achievementDaoService.get(achievementRefEnum);
        if (userAchievementDaoService.get(achievement, userWrapper) == null)
        {
          userAchievementDaoService.addAchievementToUser(achievement, userWrapper);
        }
      }
    }
  }

  private String getResolverName(Issue issue)
  {
    List<ChangeHistoryItem> changeHistoryItems = changeHistoryManager.getAllChangeItems(issue);
    for (ChangeHistoryItem changeHistoryItem : changeHistoryItems)
    {
      if (changeHistoryItem.getField().equals("status") &&
          changeHistoryItem.getTo().equals("Resolved"))
      {
        return changeHistoryItem.getUser();
      }
    }

    return null;
  }


  private Map<AchievementRefEnum, TimeRange> getTimeRangeForAchievement()
  {
    Map<AchievementRefEnum, TimeRange> timeRangeForAchievement = new HashMap<AchievementRefEnum, TimeRange>();
    timeRangeForAchievement.put(AchievementRefEnum.INSOMNIAC_USER, new TimeRange(22, 4, false));
    timeRangeForAchievement.put(AchievementRefEnum.INSOMNIAC_DEVELOPER, new TimeRange(22, 4, false));
    timeRangeForAchievement.put(AchievementRefEnum.EXPRESSO_USER, new TimeRange(4, 6, true));
    timeRangeForAchievement.put(AchievementRefEnum.EXPRESSO_DEVELOPER, new TimeRange(4, 6, true));

    return timeRangeForAchievement;
  }

  private class TimeRange
  {
    public int low;
    public int high;
    private boolean between;

    private TimeRange(int low, int high, boolean between)
    {
      this.low = low;
      this.high = high;
      this.between = between;
    }

    public boolean isBetween(int hour)
    {
      if (between)
      {
        return hour >= low && hour < high;
      }

      return hour >= low || hour < high;
    }
  }
}
