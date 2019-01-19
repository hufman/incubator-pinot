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
package org.apache.pinot.common.metadata.segment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.pinot.common.utils.EqualityUtils;


/**
 * Class for partition related column metadata:
 * <ul>
 *   <li> Partition function.</li>
 *   <li> Number of partitions. </li>
 *   <li> List of partitions. </li>
 * </ul>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = ColumnPartitionMetadata.ColumnPartitionMetadataDeserializer.class)
public class ColumnPartitionMetadata {
  private final String _functionName;
  private final int _numPartitions;
  private final List<Integer> _partitions;

  /**
   * Constructor for the class.
   * @param functionName Name of the partition function.
   * @param numPartitions Number of partitions for this column.
   * @param partitions Partitions for the column.
   */
  public ColumnPartitionMetadata(String functionName, int numPartitions, List<Integer> partitions) {
    _functionName = functionName;
    _numPartitions = numPartitions;
    _partitions = partitions;
  }

  public String getFunctionName() {
    return _functionName;
  }

  public int getNumPartitions() {
    return _numPartitions;
  }

  public List<Integer> getPartitions() {
    return _partitions;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof ColumnPartitionMetadata) {
      ColumnPartitionMetadata that = (ColumnPartitionMetadata) obj;
      return _functionName.equals(that._functionName) && _numPartitions == that._numPartitions && _partitions
          .equals(that._partitions);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hashCode = _partitions != null ? _partitions.hashCode() : 0;
    return EqualityUtils.hashCodeOf(super.hashCode(), hashCode);
  }

  /**
   * Helper method to extract partitions from configuration.
   * <p>
   * There are two format of partition strings:
   * <ul>
   *   <li>Integer format: e.g. {@code "0"}</li>
   *   <li>Range format (legacy): e.g. {@code "[0 0]"}</li>
   * </ul>
   */
  public static List<Integer> extractPartitions(List partitionList) {
    int numPartitions = partitionList.size();
    List<Integer> partitions = new ArrayList<>(numPartitions);
    for (Object o : partitionList) {
      String partitionString = o.toString();
      if (partitionString.charAt(0) == '[') {
        // Range format
        partitions.add(Integer.parseInt(partitionString.substring(1, partitionString.indexOf(' '))));
      } else {
        partitions.add(Integer.parseInt(partitionString));
      }
    }
    return partitions;
  }

  /**
   * Custom deserializer for {@link ColumnPartitionMetadata}.
   * <p>
   * This deserializer understands the legacy range format: {@code "partitionRanges":"[0 0],[1 1]"}
   */
  public static class ColumnPartitionMetadataDeserializer extends JsonDeserializer<ColumnPartitionMetadata> {
    private static final String FUNCTION_NAME_KEY = "functionName";
    private static final String NUM_PARTITIONS_KEY = "numPartitions";
    private static final String PARTITIONS_KEY = "partitions";

    // DO NOT CHANGE: for backward-compatibility
    private static final String LEGACY_PARTITIONS_KEY = "partitionRanges";
    private static final char LEGACY_PARTITION_DELIMITER = ',';

    @Override
    public ColumnPartitionMetadata deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException {
      JsonNode jsonMetadata = p.getCodec().readTree(p);
      List<Integer> partitions;
      JsonNode jsonPartitions = jsonMetadata.get(PARTITIONS_KEY);
      if (jsonPartitions != null) {
        int numPartitions = jsonPartitions.size();
        partitions = new ArrayList<>(numPartitions);
        for (int i = 0; i < numPartitions; i++) {
          partitions.add(jsonPartitions.get(i).asInt());
        }
      } else {
        // Legacy format: "partitionRanges":"[0 0],[1 1]"
        String partitionRanges = jsonMetadata.get(LEGACY_PARTITIONS_KEY).asText();
        String[] partitionStrings = StringUtils.split(partitionRanges, LEGACY_PARTITION_DELIMITER);
        partitions = new ArrayList<>(partitionStrings.length);
        for (String partitionString : partitionStrings) {
          partitions.add(Integer.parseInt(partitionString.substring(1, partitionString.indexOf(' '))));
        }
      }
      return new ColumnPartitionMetadata(jsonMetadata.get(FUNCTION_NAME_KEY).asText(),
          jsonMetadata.get(NUM_PARTITIONS_KEY).asInt(), partitions);
    }
  }
}
