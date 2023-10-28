package com.myapp.myvideoapplication.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.myapp.myvideoapplication.IntegrationTest;
import com.myapp.myvideoapplication.domain.Vlog;
import com.myapp.myvideoapplication.repository.VlogRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link VlogResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class VlogResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_HANDLE = "AAAAAAAAAA";
    private static final String UPDATED_HANDLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/vlogs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private VlogRepository vlogRepository;

    @Mock
    private VlogRepository vlogRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVlogMockMvc;

    private Vlog vlog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vlog createEntity(EntityManager em) {
        Vlog vlog = new Vlog().name(DEFAULT_NAME).handle(DEFAULT_HANDLE);
        return vlog;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vlog createUpdatedEntity(EntityManager em) {
        Vlog vlog = new Vlog().name(UPDATED_NAME).handle(UPDATED_HANDLE);
        return vlog;
    }

    @BeforeEach
    public void initTest() {
        vlog = createEntity(em);
    }

    @Test
    @Transactional
    void createVlog() throws Exception {
        int databaseSizeBeforeCreate = vlogRepository.findAll().size();
        // Create the Vlog
        restVlogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(vlog)))
            .andExpect(status().isCreated());

        // Validate the Vlog in the database
        List<Vlog> vlogList = vlogRepository.findAll();
        assertThat(vlogList).hasSize(databaseSizeBeforeCreate + 1);
        Vlog testVlog = vlogList.get(vlogList.size() - 1);
        assertThat(testVlog.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testVlog.getHandle()).isEqualTo(DEFAULT_HANDLE);
    }

    @Test
    @Transactional
    void createVlogWithExistingId() throws Exception {
        // Create the Vlog with an existing ID
        vlog.setId(1L);

        int databaseSizeBeforeCreate = vlogRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVlogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(vlog)))
            .andExpect(status().isBadRequest());

        // Validate the Vlog in the database
        List<Vlog> vlogList = vlogRepository.findAll();
        assertThat(vlogList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = vlogRepository.findAll().size();
        // set the field null
        vlog.setName(null);

        // Create the Vlog, which fails.

        restVlogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(vlog)))
            .andExpect(status().isBadRequest());

        List<Vlog> vlogList = vlogRepository.findAll();
        assertThat(vlogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkHandleIsRequired() throws Exception {
        int databaseSizeBeforeTest = vlogRepository.findAll().size();
        // set the field null
        vlog.setHandle(null);

        // Create the Vlog, which fails.

        restVlogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(vlog)))
            .andExpect(status().isBadRequest());

        List<Vlog> vlogList = vlogRepository.findAll();
        assertThat(vlogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllVlogs() throws Exception {
        // Initialize the database
        vlogRepository.saveAndFlush(vlog);

        // Get all the vlogList
        restVlogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vlog.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].handle").value(hasItem(DEFAULT_HANDLE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllVlogsWithEagerRelationshipsIsEnabled() throws Exception {
        when(vlogRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restVlogMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(vlogRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllVlogsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(vlogRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restVlogMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(vlogRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getVlog() throws Exception {
        // Initialize the database
        vlogRepository.saveAndFlush(vlog);

        // Get the vlog
        restVlogMockMvc
            .perform(get(ENTITY_API_URL_ID, vlog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(vlog.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.handle").value(DEFAULT_HANDLE));
    }

    @Test
    @Transactional
    void getNonExistingVlog() throws Exception {
        // Get the vlog
        restVlogMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingVlog() throws Exception {
        // Initialize the database
        vlogRepository.saveAndFlush(vlog);

        int databaseSizeBeforeUpdate = vlogRepository.findAll().size();

        // Update the vlog
        Vlog updatedVlog = vlogRepository.findById(vlog.getId()).get();
        // Disconnect from session so that the updates on updatedVlog are not directly saved in db
        em.detach(updatedVlog);
        updatedVlog.name(UPDATED_NAME).handle(UPDATED_HANDLE);

        restVlogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedVlog.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedVlog))
            )
            .andExpect(status().isOk());

        // Validate the Vlog in the database
        List<Vlog> vlogList = vlogRepository.findAll();
        assertThat(vlogList).hasSize(databaseSizeBeforeUpdate);
        Vlog testVlog = vlogList.get(vlogList.size() - 1);
        assertThat(testVlog.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testVlog.getHandle()).isEqualTo(UPDATED_HANDLE);
    }

    @Test
    @Transactional
    void putNonExistingVlog() throws Exception {
        int databaseSizeBeforeUpdate = vlogRepository.findAll().size();
        vlog.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVlogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, vlog.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(vlog))
            )
            .andExpect(status().isBadRequest());

        // Validate the Vlog in the database
        List<Vlog> vlogList = vlogRepository.findAll();
        assertThat(vlogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVlog() throws Exception {
        int databaseSizeBeforeUpdate = vlogRepository.findAll().size();
        vlog.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVlogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(vlog))
            )
            .andExpect(status().isBadRequest());

        // Validate the Vlog in the database
        List<Vlog> vlogList = vlogRepository.findAll();
        assertThat(vlogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVlog() throws Exception {
        int databaseSizeBeforeUpdate = vlogRepository.findAll().size();
        vlog.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVlogMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(vlog)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Vlog in the database
        List<Vlog> vlogList = vlogRepository.findAll();
        assertThat(vlogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVlogWithPatch() throws Exception {
        // Initialize the database
        vlogRepository.saveAndFlush(vlog);

        int databaseSizeBeforeUpdate = vlogRepository.findAll().size();

        // Update the vlog using partial update
        Vlog partialUpdatedVlog = new Vlog();
        partialUpdatedVlog.setId(vlog.getId());

        partialUpdatedVlog.name(UPDATED_NAME).handle(UPDATED_HANDLE);

        restVlogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVlog.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVlog))
            )
            .andExpect(status().isOk());

        // Validate the Vlog in the database
        List<Vlog> vlogList = vlogRepository.findAll();
        assertThat(vlogList).hasSize(databaseSizeBeforeUpdate);
        Vlog testVlog = vlogList.get(vlogList.size() - 1);
        assertThat(testVlog.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testVlog.getHandle()).isEqualTo(UPDATED_HANDLE);
    }

    @Test
    @Transactional
    void fullUpdateVlogWithPatch() throws Exception {
        // Initialize the database
        vlogRepository.saveAndFlush(vlog);

        int databaseSizeBeforeUpdate = vlogRepository.findAll().size();

        // Update the vlog using partial update
        Vlog partialUpdatedVlog = new Vlog();
        partialUpdatedVlog.setId(vlog.getId());

        partialUpdatedVlog.name(UPDATED_NAME).handle(UPDATED_HANDLE);

        restVlogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVlog.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVlog))
            )
            .andExpect(status().isOk());

        // Validate the Vlog in the database
        List<Vlog> vlogList = vlogRepository.findAll();
        assertThat(vlogList).hasSize(databaseSizeBeforeUpdate);
        Vlog testVlog = vlogList.get(vlogList.size() - 1);
        assertThat(testVlog.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testVlog.getHandle()).isEqualTo(UPDATED_HANDLE);
    }

    @Test
    @Transactional
    void patchNonExistingVlog() throws Exception {
        int databaseSizeBeforeUpdate = vlogRepository.findAll().size();
        vlog.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVlogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, vlog.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(vlog))
            )
            .andExpect(status().isBadRequest());

        // Validate the Vlog in the database
        List<Vlog> vlogList = vlogRepository.findAll();
        assertThat(vlogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVlog() throws Exception {
        int databaseSizeBeforeUpdate = vlogRepository.findAll().size();
        vlog.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVlogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(vlog))
            )
            .andExpect(status().isBadRequest());

        // Validate the Vlog in the database
        List<Vlog> vlogList = vlogRepository.findAll();
        assertThat(vlogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVlog() throws Exception {
        int databaseSizeBeforeUpdate = vlogRepository.findAll().size();
        vlog.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVlogMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(vlog)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Vlog in the database
        List<Vlog> vlogList = vlogRepository.findAll();
        assertThat(vlogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVlog() throws Exception {
        // Initialize the database
        vlogRepository.saveAndFlush(vlog);

        int databaseSizeBeforeDelete = vlogRepository.findAll().size();

        // Delete the vlog
        restVlogMockMvc
            .perform(delete(ENTITY_API_URL_ID, vlog.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Vlog> vlogList = vlogRepository.findAll();
        assertThat(vlogList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
