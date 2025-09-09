package com.rio.rostry.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.rio.rostry.data.local.entities.CoinTransactionEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class CoinDao_Impl implements CoinDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CoinTransactionEntity> __insertionAdapterOfCoinTransactionEntity;

  public CoinDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCoinTransactionEntity = new EntityInsertionAdapter<CoinTransactionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `coin_transactions` (`id`,`userId`,`coins`,`type`,`referenceId`,`createdAt`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CoinTransactionEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getUserId());
        statement.bindLong(3, entity.getCoins());
        statement.bindString(4, entity.getType());
        if (entity.getReferenceId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getReferenceId());
        }
        statement.bindLong(6, entity.getCreatedAt());
      }
    };
  }

  @Override
  public Object upsert(final CoinTransactionEntity tx,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCoinTransactionEntity.insert(tx);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<CoinTransactionEntity>> streamByUser(final String userId) {
    final String _sql = "SELECT * FROM coin_transactions WHERE userId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"coin_transactions"}, new Callable<List<CoinTransactionEntity>>() {
      @Override
      @NonNull
      public List<CoinTransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfCoins = CursorUtil.getColumnIndexOrThrow(_cursor, "coins");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfReferenceId = CursorUtil.getColumnIndexOrThrow(_cursor, "referenceId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<CoinTransactionEntity> _result = new ArrayList<CoinTransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CoinTransactionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final int _tmpCoins;
            _tmpCoins = _cursor.getInt(_cursorIndexOfCoins);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpReferenceId;
            if (_cursor.isNull(_cursorIndexOfReferenceId)) {
              _tmpReferenceId = null;
            } else {
              _tmpReferenceId = _cursor.getString(_cursorIndexOfReferenceId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new CoinTransactionEntity(_tmpId,_tmpUserId,_tmpCoins,_tmpType,_tmpReferenceId,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
