package repository;

import entity.ConfigJournalAppEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConfigJournalAppRepository  extends MongoRepository<ConfigJournalAppEntity, ObjectId> {


    /*
    * ConfigJournalAppEntity
        ↓
   key : value
        ↓
ConfigJournalAppRepository
        ↓
 Insert / Read / Update / Delete*/
}
