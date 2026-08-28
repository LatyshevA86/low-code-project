CREATE TABLE workflows
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    created_at  TIMESTAMPTZ  NOT NULL,
    updated_at  TIMESTAMPTZ  NOT NULL,
    created_by  BIGINT,
    updated_by  BIGINT,
    is_deleted  BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE workflow_definitions
(
    id          UUID PRIMARY KEY,
    workflow_id UUID     NOT NULL REFERENCES workflows (id),
    scheme      JSONB    NOT NULL,
    version     SMALLINT NOT NULL,
    published   BOOLEAN  NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_workflow_definitions_workflow_version UNIQUE (workflow_id, version)
);
