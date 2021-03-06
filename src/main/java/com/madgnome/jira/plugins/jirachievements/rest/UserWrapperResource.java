package com.madgnome.jira.plugins.jirachievements.rest;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.PermissionManager;
import com.madgnome.jira.plugins.jirachievements.data.services.IUserWrapperDaoService;
import com.madgnome.jira.plugins.jirachievements.services.AchievementManager;
import com.madgnome.jira.plugins.jirachievements.services.UserManager;

import javax.ws.rs.FormParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/userwrappers")
public class UserWrapperResource extends AbstractBaseResource
{
  private final IUserWrapperDaoService userWrapperDaoService;

  public UserWrapperResource(JiraAuthenticationContext jiraAuthenticationContext, PermissionManager permissionManager, UserManager userManager, AchievementManager achievementManager, IUserWrapperDaoService userWrapperDaoService)
  {
    super(jiraAuthenticationContext, permissionManager, userManager, achievementManager);
    this.userWrapperDaoService = userWrapperDaoService;
  }

  @PUT
  @Path("/activate")
  public Response activeUserWrapper(@FormParam("active") boolean active)
  {
    User user = jiraAuthenticationContext.getLoggedInUser();
    userWrapperDaoService.activate(user, active);

    return Response.ok().build();
  }
}
