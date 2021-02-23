<template>
  <el-container>
    <el-main>
      <el-container>
        <el-header class="gestion_header">
          <h1>{{ $t("notadebito.title") }}</h1>
        </el-header>
        <el-main>
          <query-builder
            :labels="qlabels"
            :rules="qrules"
            :styled="qstyled"
            :maxDepth="3"
            v-model="qbquery"
          ></query-builder>
          <el-button
            type="warning"
            icon="el-icon-search"
            circle
            @click="actualizar"
            title="Actualizar Aplicando el Filtro"
          ></el-button>
        </el-main>
      </el-container>
      <el-container>
        <el-row>
          <el-col>
            <el-table
              v-loading="loading"
              :data="tableData"
              highlight-current-row
              style="width: 100%; font-size: 12px;"
              max-height="350"
              @current-change="handleCurrentChange"
              @sort-change="handleSort"
              @filter-change="handleFilter"
            >
              <el-table-column type="selection" width="35" />
              <el-table-column type="expand" width="35">
                <template slot-scope="scope">
                  <el-table
                    :data="scope.row.items"
                    :summary-method="getSummaries"
                    show-summary
                  >
                    <el-table-column label="Detalle">
                      <template slot-scope="item">
                        <span>{{ item.row.fait_detalle }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column
                      label="Vr. Unitario"
                      align="right"
                      width="120"
                    >
                      <template slot-scope="item">
                        <span>{{
                          item.row.fait_valorunitario | currency_2("$")
                        }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column label="Qty" width="80">
                      <template slot-scope="item">
                        <span>{{ item.row.fait_cantidad }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column label="Tasa Iva" width="80">
                      <template slot-scope="item">
                        <span>{{ item.row.fait_tasaiva }}%</span>
                      </template>
                    </el-table-column>
                    <el-table-column label="Vr. Iva" align="right" width="120">
                      <template slot-scope="item">
                        <span>{{
                          item.row.fait_valoriva | currency_2("$")
                        }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column
                      prop="fait_total"
                      label="Vr. Total"
                      align="right"
                      width="120"
                    >
                      <template slot-scope="item">
                        <span>{{ item.row.fait_total | currency_2("$") }}</span>
                      </template>
                    </el-table-column>
                  </el-table>
                </template>
              </el-table-column>
              <el-table-column label="Número">
                <template slot-scope="scope">
                  <span>{{ scope.row.fact_numero }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Fecha" width="150">
                <template slot-scope="scope">
                  <span>{{ scope.row.fact_fecha | moment("YYYY-MM-DD") }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Descripción" width="350">
                <template slot-scope="scope">
                  <span>{{ scope.row.fact_descripcion }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Tp. Compr." width="100">
                <template slot-scope="scope">
                  <span>{{ scope.row.tipo_comprobante }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Comprobante" width="100">
                <template slot-scope="scope">
                  <span>{{ scope.row.id_comprobante }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Tp. Id.">
                <template slot-scope="scope">
                  <span>{{ scope.row.id_identificacion }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Identificación" width="110">
                <template slot-scope="scope">
                  <span>{{ scope.row.id_persona }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Primer Apellido" width="110">
                <template slot-scope="scope">
                  <span>{{ scope.row.primer_apellido }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Segundo Apellido" width="125">
                <template slot-scope="scope">
                  <span>{{ scope.row.segundo_apellido }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Nombre" width="200">
                <template slot-scope="scope">
                  <span>{{ scope.row.nombre }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Total" width="120" align="right">
                <template slot-scope="scope">
                  <span>{{ scope.row.fact_total | currency_2("$") }}</span>
                </template>
              </el-table-column>
            </el-table>
            <el-pagination
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              :page-size="page_size"
              layout="sizes, prev, pager, next, total"
              :total="total"
            >
            </el-pagination>
          </el-col>
        </el-row>
      </el-container>
      <el-row>
        <el-col :span="24">
          <el-button type="primary" @click="drawer = true">Nueva Nota Débito</el-button>
        </el-col>
      </el-row>
    </el-main>
    <el-drawer
      title="Nueva Nota Débito"
      :visible.sync="drawer"
      direction="rtl"
      :with-header="true"
      size="50%">
      <el-container>
        <el-main>
          <el-form :inline="true" :model="factura">
            <el-form-item label="Factura Número">
              <el-input v-model="factura.fact_numero" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" />
            </el-form-item>
          </el-form>
          <el-row :gutter="4">
            <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
              <b>Fecha</b>
            </el-col>
            <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
              <span>{{ factura.fact_fecha }}</span>
            </el-col>            
          </el-row>
        </el-main>
      </el-container>
    </el-drawer>    
  </el-container>
</template>
<script>
import VueQueryBuilder from "vue-query-builder";
import { mapGetters } from "vuex";

import { currency } from "@/utils/math";
import { getNotasDebito } from "@/api/factura";

export default {
  components: {
    "query-builder": VueQueryBuilder
  },
  computed: {
    ...mapGetters(["empresa", "usuario"])
  },
  data() {
    return {
      loading: false,
      tableData: [],
      drawer: false,
      qbquery: {},
      qrules: [
        {
          type: "custom",
          id: "fact_numero",
          label: this.$i18n.t("factura.numero"),
          operators: ["=", "<>", "<", "<=", ">", ">="]
        },
        {
          type: "text",
          inputType: "date",
          id: "fact_fecha",
          default: new Date(),
          label: this.$i18n.t("factura.fecha"),
          operators: ["contiene a"]
        }
      ],
      qlabels: {
        matchType: this.$i18n.t("qb.matchType"),
        matchTypes: [
          {
            id: "all",
            label: this.$i18n.t("qb.matchTypeAll")
          },
          {
            id: "any",
            label: this.$i18n.t("qb.matchTypeAny")
          }
        ],
        addRule: this.$i18n.t("qb.addRule"),
        removeRule: this.$i18n.t("qb.removeRule"),
        addGroup: this.$i18n.t("qb.addGroup"),
        removeGroup: this.$i18n.t("qb.removeGroup"),
        textInputPlaceholder: this.$i18n.t("qb.textInputPlaceholder")
      },
      qstyled: true,
      page_size: 30,
      current_page: 1,
      total: 0,
      order: "",
      filter: "",
      factura: {
        fact_numero: null
      }
    };
  },
  beforeMount() {
    this.getData();
  },
  methods: {
    handleFilter(filters) {
      this.filter = JSON.stringify(filters);
      this.getData();
    },
    handleCurrentChange(val) {
      this.current_page = val;
      this.getData();
    },
    handleSizeChange(val) {
      this.page_size = val;
      this.getData();
    },
    handleSort({ column, prop, order }) {
      console.log("column:" + JSON.stringify(column));
      console.log("prop:" + prop);
      console.log("order:" + order);
      if (prop !== null) {
        if (order === "ascending") {
          this.order = prop + " asc";
        } else {
          this.order = prop + " desc";
        }
        this.filter = this.qbquery;
        console.log("filter:" + JSON.stringify(this.filter));
        this.getData();
      }
    },
    getSummaries(param) {
      const { columns, data } = param;
      const sums = [];
      columns.forEach((column, index) => {
        if (index === 0) {
          sums[index] = "Total";
          return;
        }
        const values = data.map(item => Number(item[column.property]));
        if (!values.every(value => isNaN(value))) {
          sums[index] =
            "$" +
            currency(
              values.reduce((prev, curr) => {
                const value = Number(curr);
                if (!isNaN(value)) {
                  return prev + curr;
                } else {
                  return prev;
                }
              }, 0),
              0
            );
        } else {
          sums[index] = "-";
        }
      });

      return sums;
    },
    actualizar() {
      this.filter = this.qbquery;
      this.getData();
    },
    getData() {
      getNotasDebito(
        this.page_size,
        this.current_page,
        this.filter,
        this.order
      ).then(response => {
        this.total = response.data.total;
        this.tableData = response.data.data;
      });
    }
  }
};
</script>
