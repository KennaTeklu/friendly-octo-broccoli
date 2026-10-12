# 04 - State model

One state tree. Events, not blobs. Versions. Provenance.

## The 15 architecture questions

Every batch from v30 onward preserves answers to all 15.

1. What is the entity? The user, not the process.
2. What are the agents? 42 coaches, hierarchical.
3. What autonomy do agents have? Refuse, negotiate, ask for evidence.
4. How does the system remember? Dossier with event-sourced writes.
5. What context do agents share? Arc, tuning intents, dossier views.
6. How does the system handle discontinuity? Comeback is a first-class arc.
7. What does the system output? Actions for now, narrative for later.
8. Identity across boundaries? User-owned keypair, exportable.
9. State versioning? Schema version on every entity. Named migrations.
10. Reconciliation across devices? Signed events, not blob merges.
11. Audit and provenance? Every prescription carries its chain.
12. Rollback? Coach version pinned to arc.
13. Introspection? Query the provenance chain.
14. Portability? Documented export contract, separate from internal schema.
15. Provenance of rules? Every rule has a source.

## The state tree

One tree per profile. Contains dossier, arc state, chronicle, session log, tuning intent, identity key, export contract version, coach version pins.

Views are read-only projections. Nothing writes to a view.

## Event sourcing

Every write is an event with id, ts, source, type, payload, signature.

State = fold(apply, initial, events).

## Merging

Union of events, deduplicated by id. Conflicts resolved on evidence, not recency. Every merge produces a signed commit.

## Versioning

SCHEMA_VERSION constant. Every entity starts with a version field. Migrations are named functions.

## Provenance

Every prescription carries intent, arc, field values, engine version, rules, envelopes, author.

Stored, not computed.

## Rollback

Every coach carries a version. Every prescription records which version. Every arc pins its coach versions for the arc's duration.

## Portability

The export format is a documented JSON contract, separate from the internal Room schema. Signed. Versioned.

## The rule

Fast loops (session) inform slow loops (season). Never the reverse.