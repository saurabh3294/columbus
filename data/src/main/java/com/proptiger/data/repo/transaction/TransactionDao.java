/**
 * 
 */
package com.proptiger.data.repo.transaction;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.transaction.Transaction;

/**
 * @author mandeep
 *
 */
@Repository
public interface TransactionDao extends JpaRepository<Transaction, Integer> {
    @Query(nativeQuery=true, value="select T.* from transactions T left join payments TP on (T.id = TP.transaction_id and TP.status_id != 2) where TP.id is null and T.user_id = ?1 and T.status_id = 1")
    public List<Transaction> getExistingReusableTransactions(int userId);

    @Query("select T from Transaction T where T.statusId IN (?2) and T.userId = ?1 and T.typeId = 1 ")
    public List<Transaction> getTransactionsByStatusAndUser(int userId, List<Integer> status);

    @Query("select T from Transaction T where T.statusId = 2 and T.updatedAt > ?1")
    public List<Transaction> getRefundableTransactions(Date thresholdDate);

    @Query("select T from Transaction T where T.statusId = 2 and T.userId = ?1 and T.code = ?2")
    public Transaction getTransaction(int userId, String code);
    
    @Query("select T from Transaction T where T.statusId = 2 and T.code = ?1 and T.typeId = 1")
    public Transaction getNonExercisedTransactionByCode(String code);
    
    @Query("Select T from Transaction T where T.code = ?1 and T.typeId=1")
    public Transaction getTransactionByCode(String code);
    
    @Modifying
    @Query("update Transaction  set statusId = 4 where id = ?1 and statusId = 2")
    public int updateCouponAsRedeem(int transactionId);
    
    @Modifying
    @Query("update Transaction set statusId = ?2 where id = ?1 and statusId = ?3")
    public int updateTransactionStatusByOldStatus(int transactionId, int newStatusId, int oldStatusId);
}
