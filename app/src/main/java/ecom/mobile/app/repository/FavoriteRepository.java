package ecom.mobile.app.repository;

import ecom.mobile.app.model.Favorite;
import ecom.mobile.app.model.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {

    public List<Favorite> findAllByUserId(int id);

    public Optional<Favorite> findFavoriteByUserIdAndProductId(int userId, int productId);

    @Query(
            value = "select f.id, u.id as user_id, p.id as product_id, p.title from product p " +
                    "inner join favorite f on f.product_id = p.id " +
                    "inner join user u on u.id = f.user_id " +
                    "where u.id = ?1 " +
                    "order by f.id desc limit ?2",
            nativeQuery = true
    )
    List<Favorite> fetchFavoriteForSuggest(int id, int limit);
}
