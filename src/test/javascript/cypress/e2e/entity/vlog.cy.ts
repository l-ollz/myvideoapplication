import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Vlog e2e test', () => {
  const vlogPageUrl = '/vlog';
  const vlogPageUrlPattern = new RegExp('/vlog(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const vlogSample = { name: 'leverage Causeway', handle: 'panel THX' };

  let vlog;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/vlogs+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/vlogs').as('postEntityRequest');
    cy.intercept('DELETE', '/api/vlogs/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (vlog) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/vlogs/${vlog.id}`,
      }).then(() => {
        vlog = undefined;
      });
    }
  });

  it('Vlogs menu should load Vlogs page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('vlog');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Vlog').should('exist');
    cy.url().should('match', vlogPageUrlPattern);
  });

  describe('Vlog page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(vlogPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Vlog page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/vlog/new$'));
        cy.getEntityCreateUpdateHeading('Vlog');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', vlogPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/vlogs',
          body: vlogSample,
        }).then(({ body }) => {
          vlog = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/vlogs+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/vlogs?page=0&size=20>; rel="last",<http://localhost/api/vlogs?page=0&size=20>; rel="first"',
              },
              body: [vlog],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(vlogPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Vlog page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('vlog');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', vlogPageUrlPattern);
      });

      it('edit button click should load edit Vlog page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Vlog');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', vlogPageUrlPattern);
      });

      it('edit button click should load edit Vlog page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Vlog');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', vlogPageUrlPattern);
      });

      it('last delete button click should delete instance of Vlog', () => {
        cy.intercept('GET', '/api/vlogs/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('vlog').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', vlogPageUrlPattern);

        vlog = undefined;
      });
    });
  });

  describe('new Vlog page', () => {
    beforeEach(() => {
      cy.visit(`${vlogPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Vlog');
    });

    it('should create an instance of Vlog', () => {
      cy.get(`[data-cy="name"]`).type('Cotton Ergonomic').should('have.value', 'Cotton Ergonomic');

      cy.get(`[data-cy="handle"]`).type('Tactics Cambridgeshire Intelligent').should('have.value', 'Tactics Cambridgeshire Intelligent');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        vlog = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', vlogPageUrlPattern);
    });
  });
});
