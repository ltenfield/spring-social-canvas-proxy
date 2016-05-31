package org.springframework.social.canvas;

import javax.inject.Inject;

import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.FriendOperations;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {

	private Facebook facebook;

	@Inject
	public HomeController(Facebook facebook) {
		this.facebook = facebook;
	}
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String homePage(Model model) {
		String name = facebook.userOperations().getUserProfile().getName();
		model.addAttribute("name", name);
		FriendOperations friendOperations = facebook.friendOperations();
		PagedList<User> friendProfiles = friendOperations.getFriendProfiles();
		model.addAttribute("friends", friendProfiles);
		return "friends";
	}
}
