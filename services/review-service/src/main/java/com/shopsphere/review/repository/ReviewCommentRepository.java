package com.shopsphere.review.repository;

import com.shopsphere.review.model.ReviewComment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewCommentRepository extends MongoRepository<ReviewComment, String> {

    List<ReviewComment> findByReviewId(String reviewId);
}
