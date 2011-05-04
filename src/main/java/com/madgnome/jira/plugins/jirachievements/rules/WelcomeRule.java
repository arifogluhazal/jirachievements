package com.madgnome.jira.plugins.jirachievements.rules;

import com.atlassian.jira.security.JiraAuthenticationContext;
import com.madgnome.jira.plugins.jirachievements.data.ao.Achievement;
import com.madgnome.jira.plugins.jirachievements.data.ao.AchievementRefEnum;
import com.madgnome.jira.plugins.jirachievements.data.ao.UserWrapper;
import com.madgnome.jira.plugins.jirachievements.data.services.IAchievementDaoService;
import com.madgnome.jira.plugins.jirachievements.data.services.IUserAchievementDaoService;
import com.madgnome.jira.plugins.jirachievements.data.services.IUserWrapperDaoService;

public class WelcomeRule extends AbstractRule implements IRule
{
  public WelcomeRule(JiraAuthenticationContext jiraAuthenticationContext, IUserWrapperDaoService userWrapperDaoService, IAchievementDaoService achievementDaoService, IUserAchievementDaoService userAchievementDaoService)
  {
    super(jiraAuthenticationContext, userWrapperDaoService, achievementDaoService, userAchievementDaoService);
  }

  @Override
  public AchievementRefEnum getAchievementRef()
  {
    return AchievementRefEnum.WELCOME;
  }

  @Override
  public void check()
  {
    for (UserWrapper userWrapper : userWrapperDaoService.all())
    {
      execute(userWrapper);
    }
  }

  private void execute(UserWrapper userWrapper)
  {
    Achievement achievement = achievementDaoService.get(getAchievementRef());
    if (userAchievementDaoService.get(achievement, userWrapper) == null)
    {
      userAchievementDaoService.addAchievementToUser(achievement, userWrapper);
    }
  }


}
