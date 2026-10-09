package com.course.apiclient.model;

import java.util.List;

/*
 * OUR response that COMBINES two remote calls:
 *   GET /users/{id}         -> the user
 *   GET /posts?userId={id}  -> their posts
 */
public record UserSummary(Long userId, String name, String email, int postCount, List<String> postTitles) {

    public static UserSummary of(User user, List<Post> posts) {
        return new UserSummary(user.id(), user.name(), user.email(), posts.size(),
                posts.stream().map(Post::title).toList());
    }
}
