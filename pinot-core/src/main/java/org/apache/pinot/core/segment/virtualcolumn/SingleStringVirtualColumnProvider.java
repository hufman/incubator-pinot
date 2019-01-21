/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.pinot.core.segment.virtualcolumn;

import java.io.IOException;
import org.apache.pinot.common.data.FieldSpec;
import org.apache.pinot.common.utils.Pairs;
import org.apache.pinot.common.utils.Pairs.IntPair;
import org.apache.pinot.core.common.Predicate;
import org.apache.pinot.core.io.reader.BaseSingleColumnSingleValueReader;
import org.apache.pinot.core.io.reader.DataFileReader;
import org.apache.pinot.core.io.reader.impl.v1.SortedIndexReader;
import org.apache.pinot.core.io.reader.impl.v1.SortedIndexReaderImpl;
import org.apache.pinot.core.io.util.DictionaryDelegatingValueReader;
import org.apache.pinot.core.io.util.ValueReader;
import org.apache.pinot.core.segment.index.ColumnMetadata;
import org.apache.pinot.core.segment.index.readers.Dictionary;
import org.apache.pinot.core.segment.index.readers.InvertedIndexReader;
import org.apache.pinot.core.segment.index.readers.StringDictionary;


/**
 * Virtual column provider for a virtual column that contains a single string.
 */
public abstract class SingleStringVirtualColumnProvider extends BaseVirtualColumnProvider {
  protected abstract String getValue(VirtualColumnContext context);

  @Override
  public DataFileReader buildReader(VirtualColumnContext context) {
    return new IntSingleValueDataFileReader(0);
  }

  @Override
  public Dictionary buildDictionary(VirtualColumnContext context) {
    DictionaryDelegatingValueReader valueReader = new DictionaryDelegatingValueReader();
    SingleStringDictionary stringDictionary = new SingleStringDictionary(valueReader, context.getTotalDocCount(), context);
    valueReader.setDictionary(stringDictionary);
    return stringDictionary;
  }

  @Override
  public ColumnMetadata buildMetadata(VirtualColumnContext context) {
    ColumnMetadata.Builder columnMetadataBuilder = super.getColumnMetadataBuilder(context);

    columnMetadataBuilder.setCardinality(1)
        .setHasDictionary(true)
        .setHasInvertedIndex(true)
        .setFieldType(FieldSpec.FieldType.DIMENSION)
        .setDataType(FieldSpec.DataType.STRING)
        .setSingleValue(true)
        .setIsSorted(true);

    return columnMetadataBuilder.build();
  }

  @Override
  public InvertedIndexReader buildInvertedIndex(VirtualColumnContext context) {
    return new SingleStringInvertedIndex(context.getTotalDocCount());
  }

  private class SingleStringInvertedIndex extends BaseSingleColumnSingleValueReader<SortedIndexReaderImpl.Context> implements SortedIndexReader<SortedIndexReaderImpl.Context> {
    private int _length;

    public SingleStringInvertedIndex(int length) {
      _length = length;
    }
    @Override
    public IntPair getDocIds(Predicate predicate) {
      throw new UnsupportedOperationException("");
    }
    @Override
    public Pairs.IntPair getDocIds(int dictId) {
      if (dictId == 0) {
        return new Pairs.IntPair(0, _length);
      } else {
        return new Pairs.IntPair(-1, -1);
      }
    }

    @Override
    public int getInt(int row) {
      return 0;
    }

    @Override
    public int getInt(int rowId, SortedIndexReaderImpl.Context context) {
      return 0;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public SortedIndexReaderImpl.Context createContext() {
      return null;
    }
  }

  private class SingleStringDictionary extends StringDictionary {
    private int _length;
    private VirtualColumnContext _context;

    public SingleStringDictionary(ValueReader valueReader, int length, VirtualColumnContext context) {
      super(valueReader, length);

      _length = length;
      _context = context;
    }

    @Override
    public int getIntValue(int dictId) {
      throw new UnsupportedOperationException();
    }

    @Override
    public long getLongValue(int dictId) {
      throw new UnsupportedOperationException();
    }

    @Override
    public float getFloatValue(int dictId) {
      throw new UnsupportedOperationException();
    }

    @Override
    public double getDoubleValue(int dictId) {
      throw new UnsupportedOperationException();
    }

    @Override
    public int length() {
      return _length;
    }

    @Override
    public boolean isSorted() {
      return true;
    }

    @Override
    public int indexOf(Object rawValue) {
      if (rawValue.equals(getValue(_context))) {
        return 0;
      } else {
        return -1;
      }
    }

    @Override
    public String get(int dictId) {
      return getValue(_context);
    }

    @Override
    public String getStringValue(int dictId) {
      return getValue(_context);
    }

    @Override
    public void close() throws IOException {
    }
  }
}
