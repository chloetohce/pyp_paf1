package vttp2023.batch4.paf.assessment.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import vttp2023.batch4.paf.assessment.Utils;
import vttp2023.batch4.paf.assessment.models.Accommodation;
import vttp2023.batch4.paf.assessment.models.AccommodationSummary;

@Repository
public class ListingsRepository {
	
	// You may add additional dependency injections

	@Autowired
	private MongoTemplate template;

	/*
	 *	db.listings.distinct('address.suburb',
	 		{'address.country': {$regex: <country>, $options: 'i'},
			'address.suburb': {$ne: null}}
	 	)
	 */
	public List<String> getSuburbs(String country) {
		Query q = Query.query(Criteria.where("address.country")
			.regex(country, "i")
			.andOperator(Criteria.where("address.suburb")
				.ne(null)));
		return template.findDistinct(q, "address.suburb", "listings", String.class);
	}

	/*
	 *	db.listings.find({
	 		'address.suburb': {$regex: <suburb>, $options: 'i'},
			price: {$lte: 250},
			accommodates: {$gte: 2},
			min_nights: {$lte: 5}
	 	}).projection({
			name: 1, accommodates:1 1, price: 1
		}).sort({price: -1})
		
	 */
	public List<AccommodationSummary> findListings(String suburb, int persons, int duration, float priceRange) {
		Criteria criteria = Criteria.where("address.suburb")
			.regex(suburb, "i")
			.and("price").lte(priceRange)
			.and("accommodates").gte(persons)
			.and("min_nights").lte(duration);
		Query query = Query.query(criteria);
		query.fields()
			.include("_id", "name", "accommodates", "price");
		query = query.with(Sort.by(Sort.Direction.DESC, "price"));

		List<Document> results = template.find(query, Document.class, "listings");
		List<AccommodationSummary> accoms = new ArrayList<>();

		for (Document d: results) {
			AccommodationSummary summary = new AccommodationSummary();
			summary.setId(d.getString("_id"));
			summary.setName(d.getString("name"));
			summary.setAccomodates(d.getInteger("accommodates"));
			summary.setPrice(d.get("price", Number.class).floatValue());
			accoms.add(summary);
		}
		
		return accoms;
	}

	// IMPORTANT: DO NOT MODIFY THIS METHOD UNLESS REQUESTED TO DO SO
	// If this method is changed, any assessment task relying on this method will
	// not be marked
	public Optional<Accommodation> findAccommodatationById(String id) {
		Criteria criteria = Criteria.where("_id").is(id);
		Query query = Query.query(criteria);

		List<Document> result = template.find(query, Document.class, "listings");
		if (result.size() <= 0)
			return Optional.empty();

		return Optional.of(Utils.toAccommodation(result.getFirst()));
	}

}
