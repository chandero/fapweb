<template>
  <el-container>
    <el-header>
      <h1>GARANTIA</h1>
    </el-header>
    <el-main>
      <el-row>
        <el-col :span="24">
          <el-table :data="tableData" stripe max-height="600">
            <el-table-column label="ID" width="80">
              <template slot-scope="scope">
                <span>{{ scope.row.GARANTIA_ID }}</span>
              </template>
            </el-table-column>
            <el-table-column label="ES HIPOTECARIA" width="150">
              <template slot-scope="scope">
                <el-checkbox
                  disabled
                  v-model="scope.row.GARANTIA_HIPOTECARIA"
                ></el-checkbox>
              </template>
            </el-table-column>
            <el-table-column
              label="MORA MINIMA"
              width="120"
              header-align="center"
              align="right"
            >
              <template slot-scope="scope">
                <span>{{ scope.row.GARANTIA_MORA_MIN }}</span>
              </template>
            </el-table-column>
            <el-table-column
              label="MORA MAXIMA"
              width="125"
              header-align="center"
              align="right"
            >
              <template slot-scope="scope">
                <span>{{ scope.row.GARANTIA_MORA_MAX }}</span>
              </template>
            </el-table-column>
            <el-table-column
              label="PORCENTAJE"
              width="120"
              header-align="center"
              align="right"
            >
              <template slot-scope="scope">
                <span>{{ scope.row.GARANTIA_PORCENTAJE }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>
<script>
import { getGarantia } from "@/api/pe";

export default {
  data() {
    return {
      tableData: [],
    };
  },
  mounted() {
    getGarantia().then((response) => {
      this.tableData = response.data;
    });
  },
  methods: {},
};
</script>