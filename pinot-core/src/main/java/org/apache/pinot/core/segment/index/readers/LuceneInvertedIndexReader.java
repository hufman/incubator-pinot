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
package org.apache.pinot.core.segment.index.readers;

import java.io.IOException;

import org.apache.pinot.core.common.Predicate;
import org.apache.pinot.core.segment.index.ColumnMetadata;
import org.apache.pinot.core.segment.memory.PinotDataBuffer;
import org.roaringbitmap.buffer.MutableRoaringBitmap;

public class LuceneInvertedIndexReader implements InvertedIndexReader<MutableRoaringBitmap>{

  public LuceneInvertedIndexReader(PinotDataBuffer pinotDataBuffer, ColumnMetadata metadata) {
    //this is tarGz stream.
    //Need to figure out a way to initialize Lucene FileDirectory using this stream
  }

  @Override
  public MutableRoaringBitmap getDocIds(int dictId) {
    throw new UnsupportedOperationException("Predicate based evaluation not supported for Bitmap based Indexing scheme");
  }

  @Override
  public MutableRoaringBitmap getDocIds(Predicate predicate) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public void close() throws IOException {
    // TODO Auto-generated method stub
    
  }

}
