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
package org.apache.pinot.core.segment.creator.impl.inv;

import java.io.File;
import java.io.IOException;

import org.apache.pinot.common.data.FieldSpec;
import org.apache.pinot.common.data.PinotObject;
import org.apache.pinot.core.segment.creator.InvertedIndexCreator;

public class LuceneIndexCreator implements InvertedIndexCreator {

  
  private FieldSpec fieldSpec;
  private File outputDirectory;

  public LuceneIndexCreator(FieldSpec fieldSpec, File outputDirectory) {
    this.fieldSpec = fieldSpec;
    this.outputDirectory = outputDirectory;
  }

  @Override
  public void add(int dictId) {
    throw new UnsupportedOperationException("Lucene indexing not supported for dictionary encoded columns");
  }

  @Override
  public void add(int[] dictIds, int length) {
    throw new UnsupportedOperationException("Lucene indexing not supported for dictionary encoded columns");
    
  }

  @Override
  public void add(PinotObject object) {
   throw new UnsupportedOperationException("Bitmap Indexing not supported for Pinot Objects");
  }

  @Override
  public void seal() throws IOException {
    
  }
  
  @Override
  public void close() throws IOException {
    // TODO Auto-generated method stub
    
  }

}
