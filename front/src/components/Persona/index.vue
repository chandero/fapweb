<template>
  <el-container>
    <el-header>
      <span>Gestión de Persona</span>
    </el-header>
    <el-main>
      <el-form
        ref="form"
        :model="form"
        label-position="top">
        <el-row :gutter="6">
          <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
            <el-form-item prop="a.id_identificacion" label="Tipo de Documento">
              <el-select :disabled="es_bloqueo" v-model="form.a.id_identificacion" filterable>
                <el-option v-for="t in tipo_documento" :key="t.id_identificacion" :label="t.descripcion_identificacion" :value="t.id_identificacion" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="5" :lg="6" :xl="6">
            <el-form-item prop="a.id_persona" label="Número de Documento">
              <el-input :disabled="es_bloqueo" v-model="form.a.id_persona" @blur="buscarPersona" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="6" :lg="4" :xl="4">
            <el-form-item prop="a.fecha_expedicion" label="Fecha de Expedición">
              <el-date-picker v-model="form.a.fecha_expedicion"/>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="7" :lg="8" :xl="8">
            <el-form-item prop="a.lugar_expedicion" label="Lugar de Expedición">
              <el-input v-model="form.a.lugar_expedicion"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.a.id_identificacion !== 4" :gutter="4">
          <el-col :xs="24" :sm="24" :md="7" :lg="7" :xl="7">
            <el-form-item prop="a.primer_apellido" label="Primer Apellido">
              <el-input v-model="form.a.primer_apellido"/>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="7" :lg="7" :xl="7">
            <el-form-item prop="a.segundo_apellido" label="Segundo Apellido">
              <el-input v-model="form.a.segundo_apellido"/>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
            <el-form-item prop="a.nombre" label="Nombre">
              <el-input v-model="form.a.nombre"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.a.id_identificacion === 4">
          <el-form-item prop="a.nombre" label="Razón Social">
            <el-input v-model="form.a.nombre"/>
          </el-form-item>
        </el-row>
        <el-row :gutter="4">
          <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
            <el-form-item prop="a.fecha_nacimiento" label="Fecha Nacimiento">
              <el-date-picker v-model="form.a.fecha_nacimiento"/>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
            <el-form-item prop="a.id_tipo_persona" label="Tipo de Persona">
              <el-select v-model="form.a.id_tipo_persona" filterable>
                <el-option v-for="t in tipo_persona" :key="t.id" :label="t.descripcion" :value="t.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
            <el-form-item prop="a.sexo" label="Genero">
              <el-select v-model="form.a.sexo" filterable>
                <el-option v-for="t in genero" :key="t.id" :label="t.descripcion" :value="t.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
            <el-form-item prop="a.id_tipo_estado_civil" label="Estado Civil">
              <el-select v-model="form.a.id_tipo_estado_civil" filterable>
                <el-option v-for="t in tipo_estado_civil" :key="t.id" :label="t.descripcion" :value="t.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="4">
          <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
            <el-form-item prop="a.lugar_nacimiento" label="Lugar de Nacimiento">
              <el-input v-model="form.a.lugar_nacimiento"/>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
            <el-form-item prop="a.provincia_nacimiento" label="Provincia de Nacimiento">
              <el-input v-model="form.a.provincia_nacimiento"/>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
            <el-form-item prop="a.depto_nacimiento" label="Departamento de Nacimiento">
              <el-input v-model="form.a.depto_nacimiento"/>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
            <el-form-item prop="a.pais_nacimiento" label="País de Nacimiento">
              <el-input v-model="form.a.pais_nacimiento"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="4">
          <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
            <el-form-item prop="e.id_estudio" label="Nivel de Escolaridad">
              <el-select v-model="form.e.id_estudio" filterable>
                <el-option v-for="e in escolaridad" :key="e.id" :label="e.descripcion" :value="e.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
            <el-form-item prop="e.id_estrato" label="Estrato Socioeconómico">
              <el-select v-model="form.e.id_estrato" filterable>
                <el-option v-for="e in estratos" :key="e.id" :label="e.descripcion" :value="e.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
            <el-form-item prop="e.id_tipovivienda" label="Tipo de Vivienda">
              <el-select v-model="form.e.id_tipovivienda" filterable>
                <el-option v-for="e in tiposvivienda" :key="e.id" :label="e.descripcion" :value="e.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="12" :sm="12" :md="3" :lg="3" :xl="3">
            <el-form-item prop="f.es_proveedor" label="Es Proveedor">
              <el-checkbox v-model="form.f.es_proveedor" />
            </el-form-item>
          </el-col>
          <el-col :xs="12" :sm="12" :md="3" :lg="3" :xl="3">
            <el-form-item prop="f.numero_rut" label="RUT">
              <el-input :disabled="!form.f.es_proveedor" v-model="form.f.numero_rut"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-collapse v-model="activeNames">
          <ElCollapseItem name="conyuge" title="Información del Conyuge">
            <el-row :gutter="4" >
              <el-col :xs="24" :sm="24" :md="5" :lg="6" :xl="6">
                <el-form-item prop="a.id_identificacion_conyuge" label="Tipo de Documento">
                  <el-select v-model="form.a.id_identificacion_conyuge" filterable>
                    <el-option v-for="t in tipo_documento" :key="t.id_identificacion" :label="t.descripcion_identificacion" :value="t.id_identificacion" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="5" :lg="6" :xl="6">
                <el-form-item prop="a.id_conyuge" label="Número de Documento">
                  <el-input v-model="form.a.id_conyuge" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="6">
              <el-col :xs="24" :sm="24" :md="7" :lg="7" :xl="7">
                <el-form-item prop="a.primer_apellido_conyuge" label="Primer Apellido">
                  <el-input v-model="form.a.primer_apellido_conyuge" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="7" :lg="7" :xl="7">
                <el-form-item prop="a.segundo_apellido_conyuge" label="Segundo Apellido">
                  <el-input v-model="form.a.segundo_apellido_conyuge" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
                <el-form-item prop="a.nombre_conyuge" label="Nombre">
                  <el-input v-model="form.a.nombre_conyuge" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="6">
              <el-col :xs="24" :sm="24" :md="7" :lg="7" :xl="7">
                <el-form-item prop="a.ingresos_conyuge" label="Ingreso Principal">
                  <el-input v-model="form.a.ingresos_conyuge" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="7" :lg="7" :xl="7">
                <el-form-item prop="a.ingresos_conyuge_otros" label="Otros Ingresos">
                  <el-input v-model="form.a.ingresos_conyuge_otros" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
                <el-form-item prop="a.egresos_conyuge" label="Egreso Principal">
                  <el-input v-model="form.a.egresos_conyuge" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
                <el-form-item prop="a.egresos_conyuge" label="Egresos Otros">
                  <el-input v-model="form.a.otros_egresos_conyuge" />
                </el-form-item>
              </el-col>
            </el-row>
          </ElCollapseItem>
          <ElCollapseItem name="laboral" title="INFORMACION LABORAL">
            <el-row :gutter="4">
              <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
                <el-form-item prop="e.id_ocupacion" label="Ocupación">
                  <el-select v-model="form.e.id_ocupacion" filterable>
                    <el-option v-for="e in tiposocupacion" :key="e.id" :label="e.descripcion" :value="e.id" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
                <el-form-item label="Madre Cabeza de Familia">
                  <el-checkbox v-model="form.e.cabezafamilia" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
                <el-form-item prop="e.id_tipocontrato" label="Contrato">
                  <el-select v-model="form.e.id_tipocontrato" filterable>
                    <el-option v-for="e in tiposcontrato" :key="e.id" :label="e.descripcion" :value="e.id" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
                <el-form-item prop="e.descripcion_contrato" label="Descripción Contrato">
                  <el-input :disabled="form.e.id_tipocontrato != 3" v-model="form.e.descripcion_contrato"/>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="4">
              <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
                <el-form-item prop="b.profesion" label="Profesión">
                  <el-input v-model="form.b.profesion"/>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
                <el-form-item prop="b.empresa_labora" label="Empresa">
                  <el-input v-model="form.b.empresa_labora"/>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
                <el-form-item prop="b.cargo_actual" label="Cargo">
                  <el-input v-model="form.b.cargo_actual"/>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
                <el-form-item prop="b.fecha_ingreso_empresa" label="Fecha Ingreso">
                  <el-date-picker v-model="form.b.fecha_ingreso_empresa" type="date" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="4">
              <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                <el-form-item prop="b.id_ciiu" label="CIIU">
                  <el-select v-model="form.b.id_ciiu" filterable style="width: 90%;">
                    <el-option v-for="e in tiposciiu" :key="e.id" :label="e.descripcion" :value="e.id" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
                <el-form-item prop="e.declara_renta" label="Declara Renta">
                  <el-checkbox v-model="form.e.declara_renta" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
                <el-form-item prop="c.retefuente" label="Aplica Retención en la Fuente">
                  <el-checkbox v-model="form.c.retefuente" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-card class="box-card">
              <div slot="header" class="clearfix">
                <span>INGRESOS / EGRESOS</span>
              </div>
              <el-card class="box-card">
                <div slot="header" class="clearfix">
                  <span>Ingresos</span>
                </div>
                <el-row :gutter="4">
                  <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
                    <el-form-item prop="b.declaracion" label="Declaración de Fondos">
                      <el-input v-model="form.b.declaracion" />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="4">
                  <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                    <el-form-item prop="b.ingresos_a_principal" label="Ingresos Actividad Principal">
                      <el-input v-model="form.b.ingresos_a_principal" type="number" />
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                    <el-form-item prop="b.ingresos_otros" label="Ingresos Otra Actividad">
                      <el-input v-model="form.b.ingresos_otros" />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="4">
                  <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
                    <el-form-item prop="b.desc_ingr_otros" label="Descripción Otros Ingresos">
                      <el-input v-model="form.b.desc_ingr_otros" />
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-card>
              <el-card class="box-card">
                <div slot="header" class="clearfix">
                  <span>Egresos</span>
                </div>
                <el-row :gutter="4">
                  <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                    <el-form-item prop="c.egresos_alquiler" label="Arriendo">
                      <el-input v-model="form.c.egresos_alquiler" type="number" />
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                    <el-form-item prop="c.egresos_transporte" label="Transporte">
                      <el-input v-model="form.c.egresos_transporte" />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="4">
                  <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                    <el-form-item prop="c.egresos_servicios" label="Servicios">
                      <el-input v-model="form.c.egresos_servicios" />
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                    <el-form-item prop="c.egresos_alimentacion" label="Alimentación">
                      <el-input v-model="form.c.egresos_alimentacion" />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="4">
                  <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                    <el-form-item prop="c.egresos_deudas" label="Prestamos">
                      <el-input v-model="form.c.egresos_deudas" />
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                    <el-form-item prop="c.egresos_otros" label="Otros Egresos">
                      <el-input v-model="form.c.egresos_otros" />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="4">
                  <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
                    <el-form-item prop="c.desc_egre_otros" label="Descripción Otros Egresos">
                      <el-input v-model="form.c.desc_egre_otros" />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="4">
                  <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                    <el-form-item prop="c.total_activos" label="Total Activos">
                      <el-input v-model="form.c.total_activos" />
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                    <el-form-item prop="c.total_pasivos" label="Total Pasivos">
                      <el-input v-model="form.c.total_pasivos" />
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-card>
            </el-card>
          </ElCollapseItem>
          <ElCollapseItem name="direccion" title="DIRECCIONES">
            <el-card>
              <el-row>
                <el-col :xs="24" :sm="24" :md="1" :lg="1" :xl="1">
                  <span>Id</span>
                </el-col>
                <el-col :xs="24" :sm="24" :md="3" :lg="3" :xl="3">
                  <span>Tipo Dirección</span>
                </el-col>
                <el-col :xs="24" :sm="24" :md="5" :lg="5" :xl="5">
                  <span>Dirección</span>
                </el-col>
                <el-col :xs="24" :sm="24" :md="3" :lg="3" :xl="3">
                  <span>Barrio</span>
                </el-col>
                <el-col :xs="24" :sm="24" :md="4" :lg="4" :xl="4">
                  <span>Municipio</span>
                </el-col>
                <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                  <span>Teléfono</span>
                </el-col>
                <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                  <span>Teléfono</span>
                </el-col>
                <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                  <span>Teléfono</span>
                </el-col>
                <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                  <span>Teléfono</span>
                </el-col>
              </el-row>
              <el-row v-for="(d, didx) in form.direcciones" :key="d.consecutivo" :gutter="4">
                <el-col :xs="24" :sm="24" :md="1" :lg="1" :xl="1">
                  <el-form-item><span>{{ d.consecutivo }}</span></el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="3" :lg="3" :xl="3">
                  <el-form-item>
                    <el-select v-model="d.id_direccion" :placeholder="$t('credito.solicitud.tipo_direccion_select')" filterable clearable style="width: 100%;">
                      <el-option v-for="td in tiposdireccion" :key="td.id" :label="td.descripcion" :value="td.id" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="4" :lg="4" :xl="4">
                  <el-form-item><el-input v-model="d.direccion" /></el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="3" :lg="3" :xl="3">
                  <el-form-item><el-input v-model="d.barrio" /></el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="4" :lg="4" :xl="4">
                  <el-form-item>
                    <el-select v-model="d.cod_municipio" :placeholder="$t('credito.solicitud.municipio_select')" filterable clearable style="width: 100%;">
                      <el-option v-for="m in municipios" :key="m.cod_municipio" :label="m.nombre" :value="m.cod_municipio" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                  <el-form-item><el-input v-model="d.telefono1" style="font-size: 12px;" /></el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                  <el-form-item><el-input v-model="d.telefono2" style="font-size: 12px;" /></el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                  <el-form-item><el-input v-model="d.telefono3" style="font-size: 12px;" /></el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                  <el-form-item><el-input v-model="d.telefono4" style="font-size: 12px;" /></el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="1" :lg="1" :xl="1">
                  <el-button style="display: table-cell;" type="danger" size="mini" circle icon="el-icon-minus" title="Borrar Dirección" @click="borrarDireccion(didx)" />
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="2">
                  <el-input v-model="addinputdireccion" type="number" />
                </el-col>
                <el-col :span="22">
                  <el-button style="display: table-cell;" type="primary" size="mini" circle icon="el-icon-plus" title="Adicionar Nueva Dirección" @click="onAddDireccion()" />
                </el-col>
              </el-row>
            </el-card>
          </ElCollapseItem>
          <ElCollapseItem name="referencia" title="REFERENCIAS">
            <el-card>
              <el-row>
                <el-col :xs="24" :sm="24" :md="1" :lg="1" :xl="1">
                  <span>Id</span>
                </el-col>
                <el-col :xs="24" :sm="24" :md="3" :lg="3" :xl="3">
                  <span>Tipo Referencia</span>
                </el-col>
                <el-col :xs="24" :sm="24" :md="3" :lg="3" :xl="3">
                  <span>Primer Apellido</span>
                </el-col>
                <el-col :xs="24" :sm="24" :md="3" :lg="3" :xl="3">
                  <span>Segundo Apellido</span>
                </el-col>
                <el-col :xs="24" :sm="24" :md="3" :lg="3" :xl="3">
                  <span>Nombre</span>
                </el-col>
                <el-col :xs="24" :sm="24" :md="3" :lg="3" :xl="3">
                  <span>Parentesco</span>
                </el-col>
                <el-col :xs="24" :sm="24" :md="5" :lg="5" :xl="5">
                  <span>Dirección</span>
                </el-col>
                <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                  <span>Teléfono</span>
                </el-col>
              </el-row>
              <el-row v-for="(r, ridx) in form.referencias" :key="r.consecutivo" :gutter="4">
                <el-col :xs="24" :sm="24" :md="1" :lg="1" :xl="1">
                  <el-form-item><span>{{ r.consecutivo_referencia }}</span></el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="3" :lg="3" :xl="3">
                  <el-form-item>
                    <el-select v-model="r.tipo_referencia" :placeholder="$t('credito.solicitud.tipo_referencia_select')" filterable clearable style="width: 100%;">
                      <el-option v-for="tr in tiposreferencia" :key="tr.id" :label="tr.descripcion" :value="tr.id" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="3" :lg="3" :xl="3">
                  <el-form-item><el-input v-model="r.primer_apellido_referencia" /></el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="3" :lg="3" :xl="3">
                  <el-form-item><el-input v-model="r.segundo_apellido_referencia" /></el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="3" :lg="3" :xl="3">
                  <el-form-item><el-input v-model="r.nombre_referencia" /></el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="3" :lg="3" :xl="3">
                  <el-form-item>
                    <el-select v-model="r.parentesco_referencia" :placeholder="$t('credito.solicitud.tipo_parentesco_select')" filterable clearable style="width: 100%;">
                      <el-option v-for="tp in tiposparentesco" :key="tp.id" :label="tp.descripcion" :value="tp.id" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="4" :lg="4" :xl="4">
                  <el-form-item><el-input v-model="r.direccion_referencia" style="font-size: 12px;" /></el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="3" :lg="3" :xl="3">
                  <el-form-item><el-input v-model="r.telefono_referencia" style="font-size: 12px;" /></el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="1" :lg="1" :xl="1">
                  <el-button style="display: table-cell;" type="danger" size="mini" circle icon="el-icon-minus" title="Borrar Referencia" @click="borrarReferencia(ridx)" />
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="2">
                  <el-input v-model="addinputreferencia" type="number" />
                </el-col>
                <el-col :span="22">
                  <el-button style="display: table-cell;" type="primary" size="mini" circle icon="el-icon-plus" title="Adicionar Nueva Referencia" @click="onAddReferencia()" />
                </el-col>
              </el-row>
            </el-card>
          </ElCollapseItem>
          <ElCollapseItem name="beneficiario" title="BENEFICIARIOS">
            <el-card>
              <el-row>
                <el-col :xs="24" :sm="24" :md="1" :lg="1" :xl="1">
                  <span>Id</span>
                </el-col>
                <el-col :xs="24" :sm="24" :md="4" :lg="4" :xl="4">
                  <span>Primer Apellido</span>
                </el-col>
                <el-col :xs="24" :sm="24" :md="4" :lg="4" :xl="4">
                  <span>Segundo Apellido</span>
                </el-col>
                <el-col :xs="24" :sm="24" :md="4" :lg="4" :xl="4">
                  <span>Nombre</span>
                </el-col>
                <el-col :xs="24" :sm="24" :md="5" :lg="5" :xl="5">
                  <span>Parentesco</span>
                </el-col>
                <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                  <span>Porcentaje</span>
                </el-col>
                <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                  <span>Auxilio</span>
                </el-col>
              </el-row>
              <el-row v-for="(b, bidx) in form.beneficiarios" :key="b.consecutivo" :gutter="4">
                <el-col :xs="24" :sm="24" :md="1" :lg="1" :xl="1">
                  <el-form-item><span>{{ b.consecutivo }}</span></el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="4" :lg="4" :xl="4">
                  <el-form-item><el-input v-model="b.primer_apellido_referencia" /></el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="4" :lg="4" :xl="4">
                  <el-form-item><el-input v-model="b.segundo_apellido_referencia" /></el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="4" :lg="4" :xl="4">
                  <el-form-item><el-input v-model="b.nombre_referencia" /></el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="5" :lg="5" :xl="5">
                  <el-form-item>
                    <el-select v-model="b.id_parentesco" :placeholder="$t('credito.solicitud.tipo_parentesco_select')" filterable clearable style="width: 100%;">
                      <el-option v-for="tp in tiposparentesco" :key="tp.id" :label="tp.descripcion" :value="tp.id" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                  <el-form-item><el-input v-model="b.porcentaje" /></el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                  <el-form-item><el-checkbox v-model="b.auxilio" /></el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="1" :lg="1" :xl="1">
                  <el-button style="display: table-cell;" type="danger" size="mini" circle icon="el-icon-minus" title="Borrar Beneficiario" @click="borrarBeneficiario(bidx)" />
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="2">
                  <el-input v-model="addinputbeneficiario" type="number" />
                </el-col>
                <el-col :span="22">
                  <el-button style="display: table-cell;" type="primary" size="mini" circle icon="el-icon-plus" title="Adicionar Nuevo Beneficiario" @click="onAddBeneficiario()" />
                </el-col>
              </el-row>
            </el-card>
          </ElCollapseItem>
          <ElCollapseItem name="hijo" title="HIJOS">
            <el-card>
              <el-row :gutter="4">
                <el-col :xs="24" :sm="24" :md="4" :lg="4" :xl="4">
                  <el-form-item label="Número de Hijos"><el-input v-model="form.e.numero_hijos" type="number" @input="form.e.numero_hijos = parseInt($event)"/></el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="4" :lg="4" :xl="4">
                  <el-form-item label="Personas a Cargo"><el-input v-model="form.e.personas_a_cargo" type="number" @input="form.e.personas_a_cargo = parseInt($event)"/></el-form-item>
                </el-col>
              </el-row>
              <el-row>
                <el-col :xs="24" :sm="24" :md="1" :lg="1" :xl="1">
                  <span>Id</span>
                </el-col>
                <el-col :xs="24" :sm="24" :md="5" :lg="5" :xl="5">
                  <span>Primer Apellido</span>
                </el-col>
                <el-col :xs="24" :sm="24" :md="5" :lg="5" :xl="5">
                  <span>Segundo Apellido</span>
                </el-col>
                <el-col :xs="24" :sm="24" :md="5" :lg="5" :xl="5">
                  <span>Nombre</span>
                </el-col>
                <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
                  <span>Fecha Nacimiento</span>
                </el-col>
              </el-row>
              <el-row v-for="(h, hidx) in form.hijos" :key="h.consecutivo_hijo" :gutter="4">
                <el-col :xs="24" :sm="24" :md="1" :lg="1" :xl="1">
                  <el-form-item><span>{{ h.consecutivo_hijo }}</span></el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="5" :lg="5" :xl="5">
                  <el-form-item><el-input v-model="h.primer_apellido" /></el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="5" :lg="5" :xl="5">
                  <el-form-item><el-input v-model="h.segundo_apellido" /></el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="5" :lg="5" :xl="5">
                  <el-form-item><el-input v-model="h.nombre" /></el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
                  <el-form-item><el-date-picker v-model="h.fecha_nacimiento" /></el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                  <el-button style="display: table-cell;" type="danger" size="mini" circle icon="el-icon-minus" title="Borrar Hijo" @click="borrarHijo(hidx)" />
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="2">
                  <el-input v-model="addinputhijo" type="number" />
                </el-col>
                <el-col :span="22">
                  <el-button style="display: table-cell;" type="primary" size="mini" circle icon="el-icon-plus" title="Adicionar Nuevo Hijo" @click="onAddHijo()" />
                </el-col>
              </el-row>
            </el-card>
          </ElCollapseItem>
        </el-collapse>
        <el-row>
          <el-col>
            <el-button type="primary" @click="dialogPersonaVisible = true">Guardar Persona</el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-main>
    <el-dialog
      :visible.sync="dialogPersonaVisible"
      title="Guardar Información de la Persona"
      width="60%"
      append-to-body
    >
      <span>Guardar la Información?</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogPersonaVisible = false">No</el-button>
        <el-button type="primary" @click="onGuardarPersona">Sí</el-button>
      </span>
    </el-dialog>
  </el-container>
</template>
<script>
import { obtenerListaTipoIdentificacion, obtenerListaTipoPersona, obtenerListaTipoEstadoCivil,
  obtenerListaTipoNivelEscolaridad, obtenerListaTipoEstrato, obtenerListaTipoVivienda, obtenerListaTipoOcupacion, obtenerListaTipoContrato,
  obtenerListaTipoCiiu, obtenerListaTipoDireccion, obtenerListaMunicipios, obtenerListaTipoReferencia, obtenerListaTipoParentesco } from '@/api/tipos'
import { obtenerPersona, guardarPersona } from '@/api/persona'

export default {
  name: 'PersonaComponent',
  props: {
    id_identificacion: {
      type: Number,
      required: false,
      default: 3
    },
    id_persona: {
      type: String,
      required: false,
      default: null
    }
  },
  data() {
    return {
      es_bloqueo: false,
      dialogPersonaVisible: false,
      tipo_documento: [],
      tipo_persona: [],
      tipo_estado_civil: [],
      escolaridad: [],
      estratos: [],
      tiposvivienda: [],
      tiposocupacion: [],
      tiposcontrato: [],
      tiposciiu: [],
      tiposdireccion: [],
      tiposreferencia: [],
      tiposparentesco: [],
      municipios: [],
      addinputdireccion: 1,
      addinputreferencia: 1,
      addinputbeneficiario: 1,
      addinputhijo: 1,
      activeNames: ['conyuge', 'laboral', 'direccion', 'referencia', 'beneficiario', 'hijo'],
      genero: [{ id: 'M', descripcion: 'MASCULINO' }, { id: 'F', descripcion: 'FEMENINO' }, { id: 'N', descripcion: 'NINGUNO' }],
      nombre: null,
      form: {
        a: {
          id_identificacion: null,
          id_persona: null,
          lugar_expedicion: null,
          fecha_expedicion: null,
          nombre: null,
          primer_apellido: null,
          segundo_apellido: null,
          id_tipo_persona: 0,
          sexo: null,
          fecha_nacimiento: null,
          lugar_nacimiento: null,
          provincia_nacimiento: null,
          depto_nacimiento: null,
          pais_nacimiento: null,
          id_tipo_estado_civil: 0,
          id_conyuge: null,
          id_identificacion_conyuge: 0,
          nombre_conyuge: null,
          primer_apellido_conyuge: null,
          segundo_apellido_conyuge: null
        },
        b: {
          id_apoderado: null,
          id_identificacion_apoderado: 0,
          nombre_apoderado: null,
          primer_apellido_apoderado: null,
          segundo_apellido_apoderado: null,
          profesion: null,
          id_estado: 0,
          id_tipo_relacion: 0,
          id_ciiu: 0,
          empresa_labora: null,
          fecha_ingreso_empresa: null,
          cargo_actual: null,
          declaracion: null,
          ingresos_a_principal: 0,
          ingresos_otros: 0,
          ingresos_conyuge: 0,
          ingresos_conyuge_otros: 0,
          desc_ingr_otros: null
        },
        c: {
          egresos_alquiler: 0,
          egresos_servicios: 0,
          egresos_transporte: 0,
          egresos_alimentacion: 0,
          egresos_deudas: 0,
          egresos_otros: 0,
          desc_egre_otros: null,
          egresos_conyuge: 0,
          otros_egresos_conyuge: 0,
          total_activos: 0,
          total_pasivos: 0,
          educacion: null,
          retefuente: null,
          acta: null,
          fecha_registro: new Date(),
          foto: null,
          firma: null,
          escritura_constitucion: null
        },
        d: {
          duracion_sociedad: null,
          capital_social: 0,
          matricula_mercantil: null,
          foto_huella: null,
          datos_huella: null,
          email: null,
          id_empleado: null,
          fecha_actualizacion: null
        },
        e: {
          numero_hijos: null,
          id_ocupacion: 0,
          id_tipocontrato: 0,
          descripcion_contrato: null,
          id_sector: 0,
          descripcion_sector: null,
          venta_anual: 0,
          fecha_ultimo_balance: null,
          numero_empleados: null,
          declara_renta: null,
          personas_a_cargo: null,
          id_estrato: 0,
          cabezafamilia: null,
          id_estudio: 0,
          id_tipovivienda: 0
        },
        f: {
          es_proveedor: null,
          numero_rut: null
        },
        direcciones: [],
        referencias: [],
        beneficiarios: [],
        hijos: []
      },
      direccion: {
        consecutivo: null,
        id_direccion: 0,
        direccion: null,
        barrio: null,
        cod_municipio: null,
        municipio: null,
        telefono1: null,
        telefono2: null,
        telefono3: null,
        telefono4: null
      },
      referencia: {
        consecutivo_referencia: null,
        primer_apellido_referencia: null,
        segundo_apellido_referencia: null,
        nombre_referencia: null,
        direccion_referencia: null,
        telefono_referencia: null,
        tipo_referencia: 0,
        parentesco_referencia: 0
      },
      beneficiario: {
        consecutivo: null,
        primer_apellido: null,
        segundo_apellido: null,
        nombre: null,
        id_parentesco: 0,
        porcentaje: null,
        auxilio: null
      },
      hijo: {
        consecutivo_hijo: null,
        nombre: null,
        primer_apellido: null,
        segundo_apellido: null,
        fecha_nacimiento: null
      }
    }
  },
  beforeMount() {
    obtenerListaTipoIdentificacion().then(response => {
      this.tipo_documento = response.data
      obtenerListaTipoPersona().then(response => {
        this.tipo_persona = response.data
        obtenerListaTipoEstadoCivil().then(response => {
          this.tipo_estado_civil = response.data
          obtenerListaTipoNivelEscolaridad().then(response => {
            this.escolaridad = response.data
            obtenerListaTipoEstrato().then(response => {
              this.estratos = response.data
              obtenerListaTipoVivienda().then(response => {
                this.tiposvivienda = response.data
                obtenerListaTipoOcupacion().then(response => {
                  this.tiposocupacion = response.data
                  obtenerListaTipoContrato().then(response => {
                    this.tiposcontrato = response.data
                    obtenerListaTipoCiiu().then(response => {
                      this.tiposciiu = response.data
                      obtenerListaTipoDireccion().then(response => {
                        this.tiposdireccion = response.data
                        obtenerListaMunicipios().then(response => {
                          this.municipios = response.data
                          obtenerListaTipoParentesco().then(response => {
                            this.tiposparentesco = response.data
                            obtenerListaTipoReferencia().then(response => {
                              this.tiposreferencia = response.data
                              if (this.id_identificacion != null && this.id_persona != null) {
                                this.form.a.id_identificacion = this.id_identificacion
                                this.form.a.id_persona = this.id_persona
                                this.buscarPersona()
                              }
                            }).catch(error => {
                              console.log('Error consultando Tipo Referencia: ' + error)
                            })
                          }).catch(error => {
                            console.log('Error consultanto Tipo Parentesco: ' + error)
                          })
                        }).catch(error => {
                          console.log('Error consultando Municipios: ' + error)
                        })
                      }).catch(error => {
                        console.log('Error consultando Tipo Direccion: ' + error)
                      })
                    }).catch(error => {
                      console.log('Error consultando Tipo Ciiu:' + error)
                    })
                  }).catch(error => {
                    console.log('Error consultando Tipo Contrato:' + error)
                  })
                }).catch(error => {
                  console.log('Error consultando Tipo Ocupacion: ' + error)
                })
              }).catch(error => {
                console.log('Error consultando Tipos Vivienda:' + error)
              })
            }).catch(error => {
              console.log('Error consultando Estratos:' + error)
            })
          }).catch(error => {
            console.log('Error consultando Escolaridad:' + error)
          })
        }).catch(error => {
          console.log('Error consultando tipo persona:' + error)
        })
      }).catch(error => {
        console.log('Error consultando tipo persona:' + error)
      })
    }).catch(error => {
      console.log('Error consultando tipo identificación:' + error)
    })
  },
  methods: {
    buscarPersona() {
      const loading = this.$loading({
        lock: true,
        text: 'Cargando...',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      obtenerPersona(this.form.a.id_identificacion, this.form.a.id_persona).then(response => {
        loading.close()
        if (response.status === 200) {
          this.es_bloqueo = true
          this.form = response.data
          this.form.c.retefuente = !!this.form.c.retefuente
          this.form.d.declara_renta = !!this.form.d.declara_renta
          this.form.e.cabezafamilia = !!this.form.e.cabezafamilia
          this.form.e.es_proveedor = !!this.form.e.es_proveedor
          this.nombre = this.form.a.nombre + ' ' + this.form.a.primer_apellido + ' ' + this.form.a.segundo_apellido
          this.$emit('obtained', this.nombre)
          // this.form.direcciones = !!this.form.direcciones
        }
      }).catch(() => {
        loading.close()
      })
    },
    borrarDireccion(didx) {
      this.$confirm('Seguro de Borrar la Dirección. Continuar?', 'Advertencia', {
        confirmButtonText: 'Sí',
        cancelButtonText: 'No',
        type: 'warning'
      }).then(() => {
        this.form.direcciones.splice(didx, 1)
        var i = 1
        this.form.direcciones.forEach(d => {
          d.consecutivo = i
          i++
        })
        this.$message({
          type: 'success',
          message: 'Borrado Finalizado'
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: 'Borrado Cancelado'
        })
      })
    },
    borrarReferencia(didx) {
      this.$confirm('Seguro de Borrar la Referencia. Continuar?', 'Advertencia', {
        confirmButtonText: 'Sí',
        cancelButtonText: 'No',
        type: 'warning'
      }).then(() => {
        this.form.referencias.splice(didx, 1)
        this.$message({
          type: 'success',
          message: 'Borrado Finalizado'
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: 'Borrado Cancelado'
        })
      })
    },
    borrarBeneficiario(didx) {
      this.$confirm('Seguro de Borrar al Beneficiario. Continuar?', 'Advertencia', {
        confirmButtonText: 'Sí',
        cancelButtonText: 'No',
        type: 'warning'
      }).then(() => {
        this.form.beneficiarios.splice(didx, 1)
        this.$message({
          type: 'success',
          message: 'Borrado Finalizado'
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: 'Borrado Cancelado'
        })
      })
    },
    borrarHijo(didx) {
      this.$confirm('Seguro de Borrar al Hijo. Continuar?', 'Advertencia', {
        confirmButtonText: 'Sí',
        cancelButtonText: 'No',
        type: 'warning'
      }).then(() => {
        this.form.hijos.splice(didx, 1)
        this.$message({
          type: 'success',
          message: 'Borrado Finalizado'
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: 'Borrado Cancelado'
        })
      })
    },
    onAddDireccion() {
      const direccion = {
        consecutivo: null,
        id_direccion: null,
        direccion: null,
        barrio: null,
        cod_municipio: null,
        municipio: null,
        telefono1: null,
        telefono2: null,
        telefono3: null,
        telefono4: null
      }
      const csc = this.form.direcciones.length + 1
      direccion.consecutivo = parseInt(csc)
      this.form.direcciones.push(direccion)
    },
    onAddReferencia() {
      const referencia = {
        consecutivo_referencia: null,
        primer_apellido_referencia: null,
        segundo_apellido_referencia: null,
        nombre_referencia: null,
        direccion_referencia: null,
        telefono_referencia: null,
        tipo_referencia: null,
        parentesco_referencia: null
      }
      const csc = this.form.referencias.length + 1
      referencia.consecutivo_referencia = parseInt(csc)
      this.form.referencias.push(referencia)
    },
    onAddBeneficiario() {
      const beneficiario = {
        consecutivo: null,
        primer_apellido: null,
        segundo_apellido: null,
        nombre: null,
        id_parentesco: null,
        porcentaje: null,
        auxilio: null
      }
      const csc = this.form.beneficiarios.length + 1
      beneficiario.consecutivo = parseInt(csc)
      this.form.beneficiarios.push(beneficiario)
    },
    onAddHijo() {
      const hijo = {
        consecutivo_hijo: null,
        nombre: null,
        primer_apellido: null,
        segundo_apellido: null,
        fecha_nacimiento: null
      }
      const csc = this.form.hijos.length + 1
      hijo.consecutivo_hijo = parseInt(csc)
      this.form.hijos.push(hijo)
    },
    onGuardarPersona() {
      this.dialogPersonaVisible = false
      this.form.c.retefuente = this.form.c.retefuente ? 1 : 0
      this.form.e.cabezafamilia = this.form.e.cabezafamilia ? 1 : 0
      guardarPersona(this.form).then(response => {
        if (response.status === 200) {
          this.$message({
            message: 'Información guardada correctamente!',
            type: 'success'
          })
        } else {
          this.$message({
            message: 'No se pudo guardar la información, por favor verifique los datos.',
            type: 'warning'
          })
        }
      }).catch(error => {
        this.$message.error('Se presentó error guardando persona: ' + error)
      })
    }
  }
}
</script>
